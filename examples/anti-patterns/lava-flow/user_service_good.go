package main

/*
GOOD EXAMPLE: Clean Architecture ohne Lava Flow
Saubere Implementierung mit klarer Struktur, ohne toten Code.
Verwendung von Interfaces, Dependency Injection und Clean Architecture.
*/

import (
	"context"
	"crypto/rand"
	"crypto/subtle"
	"database/sql"
	"encoding/base64"
	"errors"
	"fmt"
	"time"

	"golang.org/x/crypto/argon2"
)

// ============================================================================
// Domain Layer - Business Entities
// ============================================================================

// User represents a user in the system
type User struct {
	ID        int
	Username  string
	Email     string
	Password  PasswordHash
	CreatedAt time.Time
	UpdatedAt time.Time
	IsActive  bool
}

// PasswordHash represents a hashed password
type PasswordHash struct {
	Hash string
	Salt string
}

// NewUser creates a new user with proper defaults
func NewUser(username, email, password string) (*User, error) {
	if err := validateUsername(username); err != nil {
		return nil, fmt.Errorf("invalid username: %w", err)
	}

	if err := validateEmail(email); err != nil {
		return nil, fmt.Errorf("invalid email: %w", err)
	}

	passwordHash, err := hashPassword(password)
	if err != nil {
		return nil, fmt.Errorf("failed to hash password: %w", err)
	}

	return &User{
		Username:  username,
		Email:     email,
		Password:  passwordHash,
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
		IsActive:  true,
	}, nil
}

// VerifyPassword checks if the provided password matches
func (u *User) VerifyPassword(password string) bool {
	return verifyPassword(password, u.Password)
}

// ============================================================================
// Repository Interface - Data Access Abstraction
// ============================================================================

// UserRepository defines the interface for user data access
type UserRepository interface {
	Create(ctx context.Context, user *User) error
	GetByID(ctx context.Context, id int) (*User, error)
	GetByUsername(ctx context.Context, username string) (*User, error)
	GetByEmail(ctx context.Context, email string) (*User, error)
	Update(ctx context.Context, user *User) error
	Delete(ctx context.Context, id int) error
	List(ctx context.Context, offset, limit int) ([]*User, error)
}

// ============================================================================
// Service Layer - Business Logic
// ============================================================================

// UserService handles user-related business logic
type UserService struct {
	repo  UserRepository
	cache CacheService
}

// NewUserService creates a new user service
func NewUserService(repo UserRepository, cache CacheService) *UserService {
	return &UserService{
		repo:  repo,
		cache: cache,
	}
}

// CreateUser creates a new user
func (s *UserService) CreateUser(ctx context.Context, username, email, password string) (*User, error) {
	// Check if user already exists
	existingUser, _ := s.repo.GetByUsername(ctx, username)
	if existingUser != nil {
		return nil, ErrUserAlreadyExists
	}

	existingUser, _ = s.repo.GetByEmail(ctx, email)
	if existingUser != nil {
		return nil, ErrEmailAlreadyExists
	}

	// Create new user
	user, err := NewUser(username, email, password)
	if err != nil {
		return nil, err
	}

	// Save to repository
	if err := s.repo.Create(ctx, user); err != nil {
		return nil, fmt.Errorf("failed to create user: %w", err)
	}

	// Cache the user
	s.cache.Set(ctx, fmt.Sprintf("user:%d", user.ID), user, 5*time.Minute)

	return user, nil
}

// GetUser retrieves a user by ID
func (s *UserService) GetUser(ctx context.Context, id int) (*User, error) {
	// Check cache first
	cacheKey := fmt.Sprintf("user:%d", id)
	if cached, found := s.cache.Get(ctx, cacheKey); found {
		if user, ok := cached.(*User); ok {
			return user, nil
		}
	}

	// Get from repository
	user, err := s.repo.GetByID(ctx, id)
	if err != nil {
		return nil, err
	}

	// Cache the result
	s.cache.Set(ctx, cacheKey, user, 5*time.Minute)

	return user, nil
}

// UpdateUser updates user information
func (s *UserService) UpdateUser(ctx context.Context, user *User) error {
	// Validate user data
	if err := validateUsername(user.Username); err != nil {
		return fmt.Errorf("invalid username: %w", err)
	}

	if err := validateEmail(user.Email); err != nil {
		return fmt.Errorf("invalid email: %w", err)
	}

	// Update timestamp
	user.UpdatedAt = time.Now()

	// Update in repository
	if err := s.repo.Update(ctx, user); err != nil {
		return fmt.Errorf("failed to update user: %w", err)
	}

	// Invalidate cache
	s.cache.Delete(ctx, fmt.Sprintf("user:%d", user.ID))

	return nil
}

// DeleteUser deletes a user (soft delete)
func (s *UserService) DeleteUser(ctx context.Context, id int) error {
	user, err := s.repo.GetByID(ctx, id)
	if err != nil {
		return err
	}

	// Soft delete by deactivating
	user.IsActive = false
	user.UpdatedAt = time.Now()

	if err := s.repo.Update(ctx, user); err != nil {
		return fmt.Errorf("failed to delete user: %w", err)
	}

	// Invalidate cache
	s.cache.Delete(ctx, fmt.Sprintf("user:%d", id))

	return nil
}

// AuthenticateUser authenticates a user with username and password
func (s *UserService) AuthenticateUser(ctx context.Context, username, password string) (*User, error) {
	user, err := s.repo.GetByUsername(ctx, username)
	if err != nil {
		return nil, ErrInvalidCredentials
	}

	if !user.IsActive {
		return nil, ErrUserInactive
	}

	if !user.VerifyPassword(password) {
		return nil, ErrInvalidCredentials
	}

	return user, nil
}

// ListUsers returns a paginated list of users
func (s *UserService) ListUsers(ctx context.Context, page, pageSize int) ([]*User, error) {
	offset := (page - 1) * pageSize
	return s.repo.List(ctx, offset, pageSize)
}

// ============================================================================
// Repository Implementation - MySQL
// ============================================================================

// MySQLUserRepository implements UserRepository for MySQL
type MySQLUserRepository struct {
	db *sql.DB
}

// NewMySQLUserRepository creates a new MySQL user repository
func NewMySQLUserRepository(db *sql.DB) *MySQLUserRepository {
	return &MySQLUserRepository{db: db}
}

// Create inserts a new user into the database
func (r *MySQLUserRepository) Create(ctx context.Context, user *User) error {
	query := `
		INSERT INTO users (username, email, password_hash, password_salt, 
		                  created_at, updated_at, is_active)
		VALUES (?, ?, ?, ?, ?, ?, ?)
	`

	result, err := r.db.ExecContext(ctx, query,
		user.Username, user.Email, user.Password.Hash, user.Password.Salt,
		user.CreatedAt, user.UpdatedAt, user.IsActive)

	if err != nil {
		return err
	}

	id, err := result.LastInsertId()
	if err != nil {
		return err
	}

	user.ID = int(id)
	return nil
}

// GetByID retrieves a user by ID
func (r *MySQLUserRepository) GetByID(ctx context.Context, id int) (*User, error) {
	query := `
		SELECT id, username, email, password_hash, password_salt,
		       created_at, updated_at, is_active
		FROM users
		WHERE id = ? AND is_active = true
	`

	var user User
	err := r.db.QueryRowContext(ctx, query, id).Scan(
		&user.ID, &user.Username, &user.Email,
		&user.Password.Hash, &user.Password.Salt,
		&user.CreatedAt, &user.UpdatedAt, &user.IsActive,
	)

	if err == sql.ErrNoRows {
		return nil, ErrUserNotFound
	}

	if err != nil {
		return nil, err
	}

	return &user, nil
}

// GetByUsername retrieves a user by username
func (r *MySQLUserRepository) GetByUsername(ctx context.Context, username string) (*User, error) {
	query := `
		SELECT id, username, email, password_hash, password_salt,
		       created_at, updated_at, is_active
		FROM users
		WHERE username = ?
	`

	var user User
	err := r.db.QueryRowContext(ctx, query, username).Scan(
		&user.ID, &user.Username, &user.Email,
		&user.Password.Hash, &user.Password.Salt,
		&user.CreatedAt, &user.UpdatedAt, &user.IsActive,
	)

	if err == sql.ErrNoRows {
		return nil, ErrUserNotFound
	}

	if err != nil {
		return nil, err
	}

	return &user, nil
}

// GetByEmail retrieves a user by email
func (r *MySQLUserRepository) GetByEmail(ctx context.Context, email string) (*User, error) {
	query := `
		SELECT id, username, email, password_hash, password_salt,
		       created_at, updated_at, is_active
		FROM users
		WHERE email = ?
	`

	var user User
	err := r.db.QueryRowContext(ctx, query, email).Scan(
		&user.ID, &user.Username, &user.Email,
		&user.Password.Hash, &user.Password.Salt,
		&user.CreatedAt, &user.UpdatedAt, &user.IsActive,
	)

	if err == sql.ErrNoRows {
		return nil, ErrUserNotFound
	}

	if err != nil {
		return nil, err
	}

	return &user, nil
}

// Update updates a user in the database
func (r *MySQLUserRepository) Update(ctx context.Context, user *User) error {
	query := `
		UPDATE users
		SET username = ?, email = ?, updated_at = ?, is_active = ?
		WHERE id = ?
	`

	_, err := r.db.ExecContext(ctx, query,
		user.Username, user.Email, user.UpdatedAt, user.IsActive, user.ID)

	return err
}

// Delete removes a user from the database (hard delete)
func (r *MySQLUserRepository) Delete(ctx context.Context, id int) error {
	query := `DELETE FROM users WHERE id = ?`
	_, err := r.db.ExecContext(ctx, query, id)
	return err
}

// List returns a paginated list of users
func (r *MySQLUserRepository) List(ctx context.Context, offset, limit int) ([]*User, error) {
	query := `
		SELECT id, username, email, password_hash, password_salt,
		       created_at, updated_at, is_active
		FROM users
		WHERE is_active = true
		ORDER BY created_at DESC
		LIMIT ? OFFSET ?
	`

	rows, err := r.db.QueryContext(ctx, query, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []*User
	for rows.Next() {
		var user User
		err := rows.Scan(
			&user.ID, &user.Username, &user.Email,
			&user.Password.Hash, &user.Password.Salt,
			&user.CreatedAt, &user.UpdatedAt, &user.IsActive,
		)
		if err != nil {
			return nil, err
		}
		users = append(users, &user)
	}

	return users, nil
}

// ============================================================================
// Cache Service
// ============================================================================

// CacheService defines the interface for caching
type CacheService interface {
	Get(ctx context.Context, key string) (interface{}, bool)
	Set(ctx context.Context, key string, value interface{}, ttl time.Duration)
	Delete(ctx context.Context, key string)
}

// InMemoryCache implements a simple in-memory cache
type InMemoryCache struct {
	data map[string]cacheEntry
}

type cacheEntry struct {
	value     interface{}
	expiresAt time.Time
}

// NewInMemoryCache creates a new in-memory cache
func NewInMemoryCache() *InMemoryCache {
	return &InMemoryCache{
		data: make(map[string]cacheEntry),
	}
}

// Get retrieves a value from the cache
func (c *InMemoryCache) Get(ctx context.Context, key string) (interface{}, bool) {
	entry, exists := c.data[key]
	if !exists {
		return nil, false
	}

	if time.Now().After(entry.expiresAt) {
		delete(c.data, key)
		return nil, false
	}

	return entry.value, true
}

// Set stores a value in the cache
func (c *InMemoryCache) Set(ctx context.Context, key string, value interface{}, ttl time.Duration) {
	c.data[key] = cacheEntry{
		value:     value,
		expiresAt: time.Now().Add(ttl),
	}
}

// Delete removes a value from the cache
func (c *InMemoryCache) Delete(ctx context.Context, key string) {
	delete(c.data, key)
}

// ============================================================================
// Helper Functions
// ============================================================================

// hashPassword creates a secure hash of the password
func hashPassword(password string) (PasswordHash, error) {
	salt := make([]byte, 16)
	if _, err := rand.Read(salt); err != nil {
		return PasswordHash{}, err
	}

	hash := argon2.IDKey([]byte(password), salt, 1, 64*1024, 4, 32)

	return PasswordHash{
		Hash: base64.StdEncoding.EncodeToString(hash),
		Salt: base64.StdEncoding.EncodeToString(salt),
	}, nil
}

// verifyPassword checks if a password matches the hash
func verifyPassword(password string, hash PasswordHash) bool {
	salt, err := base64.StdEncoding.DecodeString(hash.Salt)
	if err != nil {
		return false
	}

	expectedHash, err := base64.StdEncoding.DecodeString(hash.Hash)
	if err != nil {
		return false
	}

	computedHash := argon2.IDKey([]byte(password), salt, 1, 64*1024, 4, 32)

	return subtle.ConstantTimeCompare(expectedHash, computedHash) == 1
}

// validateUsername validates a username
func validateUsername(username string) error {
	if len(username) < 3 {
		return errors.New("username must be at least 3 characters")
	}
	if len(username) > 50 {
		return errors.New("username must be at most 50 characters")
	}
	return nil
}

// validateEmail validates an email address
func validateEmail(email string) error {
	if len(email) < 5 {
		return errors.New("invalid email address")
	}
	// Simple validation - in production use a proper email validation library
	if !contains(email, "@") || !contains(email, ".") {
		return errors.New("invalid email format")
	}
	return nil
}

func contains(s, substr string) bool {
	for i := 0; i < len(s); i++ {
		if s[i:i+len(substr)] == substr {
			return true
		}
	}
	return false
}

// ============================================================================
// Error Definitions
// ============================================================================

var (
	ErrUserNotFound       = errors.New("user not found")
	ErrUserAlreadyExists  = errors.New("user already exists")
	ErrEmailAlreadyExists = errors.New("email already exists")
	ErrInvalidCredentials = errors.New("invalid credentials")
	ErrUserInactive       = errors.New("user is inactive")
)

// ============================================================================
// Main Example
// ============================================================================

func main() {
	// Initialize database
	db, err := sql.Open("mysql", "user:password@tcp(localhost:3306)/dbname")
	if err != nil {
		panic(err)
	}
	defer db.Close()

	// Initialize repository and services
	userRepo := NewMySQLUserRepository(db)
	cache := NewInMemoryCache()
	userService := NewUserService(userRepo, cache)

	// Example usage
	ctx := context.Background()

	// Create a user
	user, err := userService.CreateUser(ctx, "john", "john@example.com", "securePassword123")
	if err != nil {
		fmt.Printf("Error creating user: %v\n", err)
		return
	}

	fmt.Printf("User created: %+v\n", user)

	// Authenticate user
	authUser, err := userService.AuthenticateUser(ctx, "john", "securePassword123")
	if err != nil {
		fmt.Printf("Authentication failed: %v\n", err)
		return
	}

	fmt.Printf("User authenticated: %+v\n", authUser)
}