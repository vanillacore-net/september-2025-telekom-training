package main

/*
BAD EXAMPLE: Lava Flow Anti-Pattern
Toter Code, veraltete Funktionen, redundante Implementierungen.
Niemand wagt es, alten Code zu löschen aus Angst vor unbekannten Dependencies.
*/

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"strings"
	"time"
	// "github.com/old/deprecated-lib" // Wird nicht mehr verwendet, aber niemand löscht es
)

// UserService - Hauptservice mit viel Legacy Code
type UserService struct {
	db           *sql.DB
	cache        map[string]interface{} // Alt, wird nicht mehr verwendet
	tempCache    map[int]User           // Temporärer Fix von 2018
	newCache     *UserCache             // Neuer Cache, aber alte auch noch da
	logger       *log.Logger
	oldLogger    *OldLogger // Deprecated seit 2019
	experimental bool       // Experimentelles Feature, nie fertiggestellt
}

// User - Aktuelle User Struktur
type User struct {
	ID       int
	Username string
	Email    string
	Password string
	Created  time.Time
	// Status   string // Removed in v2, but code still references it
	IsActive bool // Added in v2
}

// OldUser - Alte User Struktur, wird noch in einigen Funktionen verwendet
type OldUser struct {
	UserID   int    // Andere Benennung!
	Name     string // Username wurde zu Name
	Mail     string // Email wurde zu Mail
	Pass     string // Password wurde zu Pass
	RegDate  string // String statt time.Time!
	Status   int    // 0=inactive, 1=active, 2=banned - jetzt IsActive bool
	UserType string // "admin", "user", "guest" - entfernt in v2
}

// LegacyUser - Noch ältere Version, wird in einer Funktion referenziert
type LegacyUser struct {
	Id       string // War mal string!
	Username string
	// Viele fehlende Felder
}

// CreateUser - Aktuelle Implementierung
func (s *UserService) CreateUser(username, email, password string) (*User, error) {
	// Neue Implementierung
	user := &User{
		Username: username,
		Email:    email,
		Password: password, // TODO: Hash password (seit 2020)
		Created:  time.Now(),
		IsActive: true,
	}

	query := `INSERT INTO users (username, email, password, created, is_active) 
	          VALUES (?, ?, ?, ?, ?)`
	result, err := s.db.Exec(query, user.Username, user.Email, user.Password,
		user.Created, user.IsActive)
	if err != nil {
		return nil, err
	}

	id, _ := result.LastInsertId()
	user.ID = int(id)

	// Update new cache
	if s.newCache != nil {
		s.newCache.Set(user.ID, user)
	}

	// Auch alten Cache updaten - warum?
	s.tempCache[user.ID] = *user

	return user, nil
}

// CreateUserOld - Alte Version, aber noch referenziert
// Deprecated: Use CreateUser instead
func (s *UserService) CreateUserOld(name, mail, pass string) (int, error) {
	// Alte Implementierung mit OldUser
	oldUser := OldUser{
		Name:    name,
		Mail:    mail,
		Pass:    pass,
		RegDate: time.Now().Format("2006-01-02"), // String Format!
		Status:  1,                                // Magic number
	}

	// Altes SQL Schema
	query := `INSERT INTO users_old (name, mail, pass, reg_date, status) 
	          VALUES (?, ?, ?, ?, ?)`
	result, err := s.db.Exec(query, oldUser.Name, oldUser.Mail, oldUser.Pass,
		oldUser.RegDate, oldUser.Status)
	if err != nil {
		// Fallback auf neue Tabelle - gefährlich!
		log.Println("Old table failed, trying new table")
		return 0, s.createUserFallback(name, mail, pass)
	}

	id, _ := result.LastInsertId()
	oldUser.UserID = int(id)

	// Cache in altem Format
	s.cache[fmt.Sprintf("user_%d", id)] = oldUser

	return oldUser.UserID, nil
}

// createUserFallback - Workaround für Bug #1234 (von 2017)
func (s *UserService) createUserFallback(name, mail, pass string) error {
	// Temporärer Fix, der permanent wurde
	tempQuery := `INSERT INTO users_temp (username, email) VALUES (?, ?)`
	_, err := s.db.Exec(tempQuery, name, mail)
	// Password wird ignoriert!
	return err
}

// CreateUserLegacy - Ganz alte Version, wird aber noch irgendwo aufgerufen
func (s *UserService) CreateUserLegacy(username string) string {
	// Verwendet das ganz alte LegacyUser Format
	legacyUser := LegacyUser{
		Id:       generateLegacyID(), // Custom ID generation
		Username: username,
	}

	// Schreibt in eine Datei statt Datenbank!
	data, _ := json.Marshal(legacyUser)
	writeToLegacyFile(string(data))

	return legacyUser.Id
}

// CreateUserExperimental - Nie fertiggestelltes Feature
func (s *UserService) CreateUserExperimental(data map[string]interface{}) error {
	if !s.experimental {
		panic("Experimental features not enabled")
	}

	// Unvollständige Implementierung
	fmt.Println("Creating user with:", data)

	// TODO: Implement this properly
	// NOTE: Don't use this in production!
	// FIXME: This doesn't actually create a user

	return fmt.Errorf("not implemented")
}

// CreateUserV3 - Angefangene neue Version, nie fertig
func (s *UserService) CreateUserV3(userData UserDataV3) (*UserV3, error) {
	// Neue Struktur, die nie verwendet wird
	panic("V3 not ready yet")
}

// GetUser - Aktuelle Get Funktion
func (s *UserService) GetUser(id int) (*User, error) {
	// Check new cache first
	if s.newCache != nil {
		if user := s.newCache.Get(id); user != nil {
			return user, nil
		}
	}

	// Check temp cache (warum gibt es den noch?)
	if user, ok := s.tempCache[id]; ok {
		return &user, nil
	}

	// Check old cache mit string key
	if val, ok := s.cache[fmt.Sprintf("user_%d", id)]; ok {
		// Konvertiere OldUser zu User - fehleranfällig!
		if oldUser, ok := val.(OldUser); ok {
			return s.convertOldUserToUser(oldUser), nil
		}
	}

	// Query database
	var user User
	query := `SELECT id, username, email, password, created, is_active FROM users WHERE id = ?`
	err := s.db.QueryRow(query, id).Scan(&user.ID, &user.Username, &user.Email,
		&user.Password, &user.Created, &user.IsActive)
	if err != nil {
		// Versuche alte Tabelle
		return s.getUserFromOldTable(id)
	}

	return &user, nil
}

// GetUserOld - Alte Get Funktion
// Deprecated: Use GetUser instead
func (s *UserService) GetUserOld(userID int) *OldUser {
	// Verwendet altes Schema
	var oldUser OldUser
	query := `SELECT user_id, name, mail, pass, reg_date, status FROM users_old WHERE user_id = ?`
	s.db.QueryRow(query, userID).Scan(&oldUser.UserID, &oldUser.Name,
		&oldUser.Mail, &oldUser.Pass, &oldUser.RegDate, &oldUser.Status)
	return &oldUser
}

// GetUserByUsername - Duplizierte Logik
func (s *UserService) GetUserByUsername(username string) (*User, error) {
	// Fast identisch mit GetUser, aber eigene Implementierung
	var user User
	query := `SELECT id, username, email, password, created, is_active FROM users WHERE username = ?`
	err := s.db.QueryRow(query, username).Scan(&user.ID, &user.Username,
		&user.Email, &user.Password, &user.Created, &user.IsActive)
	return &user, err
}

// GetUserByName - Alte Version von GetUserByUsername
func (s *UserService) GetUserByName(name string) (*OldUser, error) {
	// Verwendet altes Schema und alte Struktur
	var oldUser OldUser
	query := `SELECT user_id, name, mail, pass, reg_date, status FROM users_old WHERE name = ?`
	err := s.db.QueryRow(query, name).Scan(&oldUser.UserID, &oldUser.Name,
		&oldUser.Mail, &oldUser.Pass, &oldUser.RegDate, &oldUser.Status)
	return &oldUser, err
}

// getUserFromOldTable - Helper für Migration, die nie abgeschlossen wurde
func (s *UserService) getUserFromOldTable(id int) (*User, error) {
	oldUser := s.GetUserOld(id)
	if oldUser.UserID == 0 {
		return nil, fmt.Errorf("user not found")
	}
	return s.convertOldUserToUser(*oldUser), nil
}

// convertOldUserToUser - Konvertierung zwischen Formaten
func (s *UserService) convertOldUserToUser(old OldUser) *User {
	// Fehleranfällige Konvertierung
	created, _ := time.Parse("2006-01-02", old.RegDate)
	return &User{
		ID:       old.UserID,
		Username: old.Name,
		Email:    old.Mail,
		Password: old.Pass,
		Created:  created,
		IsActive: old.Status == 1, // Magic number conversion
	}
}

// UpdateUser - Aktuelle Update Funktion
func (s *UserService) UpdateUser(user *User) error {
	query := `UPDATE users SET username=?, email=?, is_active=? WHERE id=?`
	_, err := s.db.Exec(query, user.Username, user.Email, user.IsActive, user.ID)

	// Update all caches (warum alle?)
	if s.newCache != nil {
		s.newCache.Set(user.ID, user)
	}
	s.tempCache[user.ID] = *user
	s.cache[fmt.Sprintf("user_%d", user.ID)] = user

	return err
}

// UpdateUserOld - Alte Update Funktion
func (s *UserService) UpdateUserOld(userID int, name, mail string) error {
	// Verwendet altes Schema
	query := `UPDATE users_old SET name=?, mail=? WHERE user_id=?`
	_, err := s.db.Exec(query, name, mail, userID)
	return err
}

// UpdateUserStatus - Sollte durch UpdateUser ersetzt werden
// Deprecated: Status field removed in v2
func (s *UserService) UpdateUserStatus(userID int, status int) error {
	// Status existiert nicht mehr, aber Funktion noch da
	log.Println("WARNING: UpdateUserStatus is deprecated")

	// Versucht trotzdem zu updaten
	query := `UPDATE users SET status=? WHERE id=?`
	_, err := s.db.Exec(query, status, userID)
	if err != nil {
		// Fallback auf IsActive
		isActive := status == 1
		query = `UPDATE users SET is_active=? WHERE id=?`
		_, err = s.db.Exec(query, isActive, userID)
	}
	return err
}

// DeleteUser - Löscht nicht wirklich
func (s *UserService) DeleteUser(id int) error {
	// Soft delete in neuer Tabelle
	query := `UPDATE users SET is_active=false WHERE id=?`
	s.db.Exec(query, id)

	// Hard delete in alter Tabelle - inkonsistent!
	query = `DELETE FROM users_old WHERE user_id=?`
	s.db.Exec(query, id)

	// Cache cleanup vergessen für tempCache
	delete(s.cache, fmt.Sprintf("user_%d", id))
	if s.newCache != nil {
		s.newCache.Delete(id)
	}
	// tempCache wird nicht geleert!

	return nil
}

// DeleteUserPermanently - Duplizierte Delete Logik
func (s *UserService) DeleteUserPermanently(id int) error {
	// Fast identisch mit DeleteUser
	query := `DELETE FROM users WHERE id=?`
	_, err := s.db.Exec(query, id)
	return err
}

// DeleteUserSoft - Noch eine Delete Variante
func (s *UserService) DeleteUserSoft(id int) error {
	return s.UpdateUserStatus(id, 0) // Status 0 = deleted?
}

// AuthenticateUser - Aktuelle Auth Funktion
func (s *UserService) AuthenticateUser(username, password string) (*User, error) {
	user, err := s.GetUserByUsername(username)
	if err != nil {
		return nil, err
	}

	// Plaintext password comparison - unsicher!
	if user.Password != password {
		return nil, fmt.Errorf("invalid credentials")
	}

	return user, nil
}

// AuthenticateUserOld - Alte Auth Funktion
func (s *UserService) AuthenticateUserOld(name, pass string) bool {
	oldUser, _ := s.GetUserByName(name)
	return oldUser != nil && oldUser.Pass == pass
}

// AuthenticateUserLegacy - Ganz alte Auth
func (s *UserService) AuthenticateUserLegacy(username string) bool {
	// Liest aus Legacy File
	data := readFromLegacyFile()
	return strings.Contains(data, username)
}

// ValidateUser - Unbenutzte Validierung
func (s *UserService) ValidateUser(user *User) []string {
	// Wurde nie fertiggestellt
	var errors []string

	// TODO: Implement validation
	// if len(user.Username) < 3 {
	//     errors = append(errors, "Username too short")
	// }

	return errors
}

// MigrateUsers - Migration die nie fertig wurde
func (s *UserService) MigrateUsers() error {
	log.Println("Starting migration...")

	// Phase 1: Copy from old to new (implementiert)
	rows, err := s.db.Query(`SELECT user_id, name, mail, pass, reg_date, status FROM users_old`)
	if err != nil {
		return err
	}
	defer rows.Close()

	for rows.Next() {
		var old OldUser
		rows.Scan(&old.UserID, &old.Name, &old.Mail, &old.Pass, &old.RegDate, &old.Status)

		// Konvertiere und speichere
		// new := s.convertOldUserToUser(old)
		// s.CreateUser(new.Username, new.Email, new.Password)
	}

	// Phase 2: Verify migration (nicht implementiert)
	// TODO: Implement verification

	// Phase 3: Delete old data (nicht implementiert)
	// TODO: Delete old tables - DANGEROUS!

	log.Println("Migration incomplete")
	return nil
}

// CleanupOldData - Cleanup das nie ausgeführt wird
func (s *UserService) CleanupOldData() {
	// Sollte alte Daten löschen, tut es aber nicht
	log.Println("Cleanup scheduled for later")
	// TODO: Actually clean up
}

// Utility Functions die nicht mehr verwendet werden

func generateLegacyID() string {
	// Alte ID Generation
	return fmt.Sprintf("USER_%d", time.Now().Unix())
}

func writeToLegacyFile(data string) {
	// Schreibt in eine Datei die niemand mehr liest
	// file, _ := os.OpenFile("users_legacy.txt", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	// file.WriteString(data + "\n")
	// file.Close()
}

func readFromLegacyFile() string {
	// Liest aus einer Datei die vielleicht nicht existiert
	// data, _ := ioutil.ReadFile("users_legacy.txt")
	// return string(data)
	return ""
}

// OldLogger - Deprecated Logger
type OldLogger struct {
	// Alte Logger Implementierung
}

func (l *OldLogger) Log(message string) {
	// Old logging logic
	fmt.Println("[OLD]", message)
}

// UserCache - Neuer Cache, aber alte bleiben auch
type UserCache struct {
	data map[int]*User
}

func (c *UserCache) Get(id int) *User {
	return c.data[id]
}

func (c *UserCache) Set(id int, user *User) {
	if c.data == nil {
		c.data = make(map[int]*User)
	}
	c.data[id] = user
}

func (c *UserCache) Delete(id int) {
	delete(c.data, id)
}

// UserDataV3 - Struktur für V3 die nie kam
type UserDataV3 struct {
	Username   string
	Email      string
	Password   string
	Profile    ProfileV3
	Settings   SettingsV3
	Metadata   map[string]interface{}
	CreatedAt  time.Time
	ModifiedAt time.Time
}

// ProfileV3 - Unbenutzt
type ProfileV3 struct {
	FirstName string
	LastName  string
	Avatar    string
}

// SettingsV3 - Unbenutzt
type SettingsV3 struct {
	Theme    string
	Language string
}

// UserV3 - Unbenutzt
type UserV3 struct {
	ID   string // UUID statt int geplant
	Data UserDataV3
}

// TestFunction1 - Test Funktion die in Produktion ist
func (s *UserService) TestFunction1() {
	fmt.Println("This is a test")
	// Sollte nicht in Produktion sein
}

// DebugUser - Debug Funktion in Produktion
func (s *UserService) DebugUser(id int) {
	user, _ := s.GetUser(id)
	fmt.Printf("DEBUG: %+v\n", user)
	// Gibt sensitive Daten aus!
}

// Noch mehr unbenutzter Code...

func main() {
	// Example usage
	db, _ := sql.Open("mysql", "user:password@/dbname")
	service := &UserService{
		db:        db,
		cache:     make(map[string]interface{}),
		tempCache: make(map[int]User),
		newCache:  &UserCache{},
	}

	// Verschiedene Arten einen User zu erstellen
	service.CreateUser("john", "john@example.com", "password123")
	service.CreateUserOld("jane", "jane@example.com", "pass456")
	service.CreateUserLegacy("bob")
	// service.CreateUserExperimental(map[string]interface{}{"name": "alice"})
	// service.CreateUserV3(UserDataV3{Username: "charlie"})

	// Clean up that never happens
	service.CleanupOldData()
}