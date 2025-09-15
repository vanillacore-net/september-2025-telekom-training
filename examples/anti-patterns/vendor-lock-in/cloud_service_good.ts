// GOOD EXAMPLE: Provider-agnostische Cloud-Architektur
// Abstraktion der Cloud-Services ermöglicht einfache Migration zwischen Providern.
// Hexagonal Architecture mit Ports & Adapters Pattern.

// ============================================================================
// Domain Layer - Provider-unabhängige Interfaces
// ============================================================================

// Storage Interface
interface StorageService {
    upload(key: string, data: Buffer, contentType: string): Promise<string>;
    download(key: string): Promise<Buffer>;
    delete(key: string): Promise<void>;
    list(prefix: string): Promise<string[]>;
}

// Database Interface
interface DatabaseService {
    put(table: string, item: Record<string, any>): Promise<void>;
    get(table: string, key: Record<string, any>): Promise<any>;
    query(table: string, conditions: QueryCondition[]): Promise<any[]>;
    delete(table: string, key: Record<string, any>): Promise<void>;
}

interface QueryCondition {
    field: string;
    operator: 'eq' | 'gt' | 'lt' | 'contains';
    value: any;
}

// Authentication Interface
interface AuthService {
    createUser(email: string, password: string): Promise<User>;
    authenticate(email: string, password: string): Promise<AuthToken>;
    verifyToken(token: string): Promise<User>;
    deleteUser(userId: string): Promise<void>;
}

interface User {
    id: string;
    email: string;
    createdAt: Date;
}

interface AuthToken {
    token: string;
    expiresAt: Date;
}

// Messaging Interface
interface MessagingService {
    publishMessage(topic: string, message: any): Promise<void>;
    subscribeToTopic(topic: string, handler: MessageHandler): Promise<void>;
    sendToQueue(queue: string, message: any): Promise<void>;
    receiveFromQueue(queue: string): Promise<any[]>;
}

type MessageHandler = (message: any) => Promise<void>;

// Monitoring Interface
interface MonitoringService {
    logMetric(name: string, value: number, unit?: string): Promise<void>;
    logEvent(event: string, properties?: Record<string, any>): Promise<void>;
    createAlert(name: string, condition: AlertCondition): Promise<void>;
}

interface AlertCondition {
    metric: string;
    threshold: number;
    comparison: 'gt' | 'lt' | 'eq';
}

// Event Streaming Interface
interface EventStreamService {
    publishEvent(stream: string, event: Event): Promise<void>;
    consumeEvents(stream: string, handler: EventHandler): Promise<void>;
    createStream(name: string, partitions: number): Promise<void>;
}

interface Event {
    id: string;
    type: string;
    data: any;
    timestamp: Date;
}

type EventHandler = (event: Event) => Promise<void>;

// AI Service Interface
interface AIService {
    analyzeImage(image: Buffer): Promise<ImageAnalysis>;
    analyzeSentiment(text: string): Promise<SentimentAnalysis>;
    translateText(text: string, targetLanguage: string): Promise<string>;
}

interface ImageAnalysis {
    labels: Array<{ name: string; confidence: number }>;
}

interface SentimentAnalysis {
    sentiment: 'positive' | 'negative' | 'neutral' | 'mixed';
    confidence: number;
}

// ============================================================================
// Application Layer - Business Logic (Provider-unabhängig)
// ============================================================================

class UserService {
    constructor(
        private storage: StorageService,
        private database: DatabaseService,
        private auth: AuthService,
        private messaging: MessagingService
    ) {}
    
    async createUser(email: string, password: string): Promise<User> {
        // Provider-unabhängige User-Erstellung
        const user = await this.auth.createUser(email, password);
        
        // Provider-unabhängige Datenspeicherung
        await this.database.put('users', {
            userId: user.id,
            email: user.email,
            createdAt: user.createdAt
        });
        
        // Provider-unabhängige Benachrichtigung
        await this.messaging.publishMessage('user-events', {
            event: 'user.created',
            userId: user.id,
            timestamp: new Date()
        });
        
        return user;
    }
    
    async uploadProfilePicture(userId: string, imageBuffer: Buffer): Promise<string> {
        // Provider-unabhängiger Upload
        const key = `users/${userId}/profile.jpg`;
        const url = await this.storage.upload(key, imageBuffer, 'image/jpeg');
        
        // Update user record
        await this.database.put('users', {
            userId: userId,
            profilePictureUrl: url
        });
        
        return url;
    }
}

class OrderProcessor {
    constructor(
        private database: DatabaseService,
        private messaging: MessagingService,
        private monitoring: MonitoringService
    ) {}
    
    async processOrder(order: Order): Promise<void> {
        // Provider-unabhängige Order-Verarbeitung
        await this.database.put('orders', {
            orderId: order.id,
            customerId: order.customerId,
            amount: order.amount,
            status: 'processing'
        });
        
        // Provider-unabhängige Message-Queue
        await this.messaging.sendToQueue('order-processing', order);
        
        // Provider-unabhängiges Monitoring
        await this.monitoring.logMetric('orders.processed', 1);
        await this.monitoring.logEvent('order.processing.started', {
            orderId: order.id,
            amount: order.amount
        });
    }
}

interface Order {
    id: string;
    customerId: string;
    amount: number;
    items: any[];
}

// ============================================================================
// Infrastructure Layer - Provider-spezifische Implementierungen
// ============================================================================

// AWS Implementations
import { S3, DynamoDB, Cognito, SNS, SQS, CloudWatch } from 'aws-sdk';

class AWSStorageAdapter implements StorageService {
    private s3: S3;
    
    constructor(private config: AWSConfig) {
        this.s3 = new S3({ region: config.region });
    }
    
    async upload(key: string, data: Buffer, contentType: string): Promise<string> {
        const result = await this.s3.upload({
            Bucket: this.config.bucketName,
            Key: key,
            Body: data,
            ContentType: contentType
        }).promise();
        return result.Location;
    }
    
    async download(key: string): Promise<Buffer> {
        const result = await this.s3.getObject({
            Bucket: this.config.bucketName,
            Key: key
        }).promise();
        return result.Body as Buffer;
    }
    
    async delete(key: string): Promise<void> {
        await this.s3.deleteObject({
            Bucket: this.config.bucketName,
            Key: key
        }).promise();
    }
    
    async list(prefix: string): Promise<string[]> {
        const result = await this.s3.listObjectsV2({
            Bucket: this.config.bucketName,
            Prefix: prefix
        }).promise();
        return result.Contents?.map(obj => obj.Key!) || [];
    }
}

// Azure Implementation
import { BlobServiceClient } from '@azure/storage-blob';

class AzureStorageAdapter implements StorageService {
    private blobService: BlobServiceClient;
    
    constructor(private config: AzureConfig) {
        this.blobService = BlobServiceClient.fromConnectionString(config.connectionString);
    }
    
    async upload(key: string, data: Buffer, contentType: string): Promise<string> {
        const containerClient = this.blobService.getContainerClient(this.config.containerName);
        const blockBlobClient = containerClient.getBlockBlobClient(key);
        await blockBlobClient.upload(data, data.length, {
            blobHTTPHeaders: { blobContentType: contentType }
        });
        return blockBlobClient.url;
    }
    
    async download(key: string): Promise<Buffer> {
        const containerClient = this.blobService.getContainerClient(this.config.containerName);
        const blobClient = containerClient.getBlobClient(key);
        const downloadResponse = await blobClient.download();
        const chunks: Buffer[] = [];
        for await (const chunk of downloadResponse.readableStreamBody!) {
            chunks.push(Buffer.from(chunk));
        }
        return Buffer.concat(chunks);
    }
    
    async delete(key: string): Promise<void> {
        const containerClient = this.blobService.getContainerClient(this.config.containerName);
        await containerClient.deleteBlob(key);
    }
    
    async list(prefix: string): Promise<string[]> {
        const containerClient = this.blobService.getContainerClient(this.config.containerName);
        const results: string[] = [];
        for await (const blob of containerClient.listBlobsFlat({ prefix })) {
            results.push(blob.name);
        }
        return results;
    }
}

// Google Cloud Implementation
import { Storage as GCPStorage } from '@google-cloud/storage';

class GCPStorageAdapter implements StorageService {
    private storage: GCPStorage;
    
    constructor(private config: GCPConfig) {
        this.storage = new GCPStorage({
            projectId: config.projectId,
            keyFilename: config.keyFile
        });
    }
    
    async upload(key: string, data: Buffer, contentType: string): Promise<string> {
        const bucket = this.storage.bucket(this.config.bucketName);
        const file = bucket.file(key);
        await file.save(data, {
            metadata: { contentType }
        });
        return `https://storage.googleapis.com/${this.config.bucketName}/${key}`;
    }
    
    async download(key: string): Promise<Buffer> {
        const bucket = this.storage.bucket(this.config.bucketName);
        const [data] = await bucket.file(key).download();
        return data;
    }
    
    async delete(key: string): Promise<void> {
        const bucket = this.storage.bucket(this.config.bucketName);
        await bucket.file(key).delete();
    }
    
    async list(prefix: string): Promise<string[]> {
        const bucket = this.storage.bucket(this.config.bucketName);
        const [files] = await bucket.getFiles({ prefix });
        return files.map(file => file.name);
    }
}

// ============================================================================
// Configuration & Factory Pattern
// ============================================================================

interface CloudConfig {
    provider: 'aws' | 'azure' | 'gcp' | 'local';
    aws?: AWSConfig;
    azure?: AzureConfig;
    gcp?: GCPConfig;
}

interface AWSConfig {
    region: string;
    bucketName: string;
    tableName: string;
}

interface AzureConfig {
    connectionString: string;
    containerName: string;
}

interface GCPConfig {
    projectId: string;
    keyFile: string;
    bucketName: string;
}

// Factory für Provider-Auswahl
class CloudServiceFactory {
    static createStorageService(config: CloudConfig): StorageService {
        switch (config.provider) {
            case 'aws':
                return new AWSStorageAdapter(config.aws!);
            case 'azure':
                return new AzureStorageAdapter(config.azure!);
            case 'gcp':
                return new GCPStorageAdapter(config.gcp!);
            case 'local':
                return new LocalStorageAdapter();
            default:
                throw new Error(`Unsupported provider: ${config.provider}`);
        }
    }
    
    static createDatabaseService(config: CloudConfig): DatabaseService {
        // Similar factory logic for database services
        switch (config.provider) {
            case 'aws':
                return new AWSDynamoDBAdapter(config.aws!);
            case 'azure':
                return new AzureCosmosDBAdapter(config.azure!);
            case 'gcp':
                return new GCPFirestoreAdapter(config.gcp!);
            case 'local':
                return new LocalDatabaseAdapter();
            default:
                throw new Error(`Unsupported provider: ${config.provider}`);
        }
    }
    
    // Weitere Factory-Methoden für andere Services...
}

// Local/On-Premise Implementation für Tests
class LocalStorageAdapter implements StorageService {
    private storage = new Map<string, Buffer>();
    
    async upload(key: string, data: Buffer, contentType: string): Promise<string> {
        this.storage.set(key, data);
        return `file:///${key}`;
    }
    
    async download(key: string): Promise<Buffer> {
        const data = this.storage.get(key);
        if (!data) throw new Error(`File not found: ${key}`);
        return data;
    }
    
    async delete(key: string): Promise<void> {
        this.storage.delete(key);
    }
    
    async list(prefix: string): Promise<string[]> {
        return Array.from(this.storage.keys()).filter(key => key.startsWith(prefix));
    }
}

class LocalDatabaseAdapter implements DatabaseService {
    private tables = new Map<string, Map<string, any>>();
    
    async put(table: string, item: Record<string, any>): Promise<void> {
        if (!this.tables.has(table)) {
            this.tables.set(table, new Map());
        }
        const tableData = this.tables.get(table)!;
        const key = JSON.stringify(item.id || item.userId || item.orderId);
        tableData.set(key, item);
    }
    
    async get(table: string, key: Record<string, any>): Promise<any> {
        const tableData = this.tables.get(table);
        if (!tableData) return null;
        return tableData.get(JSON.stringify(key));
    }
    
    async query(table: string, conditions: QueryCondition[]): Promise<any[]> {
        const tableData = this.tables.get(table);
        if (!tableData) return [];
        
        const results: any[] = [];
        for (const item of tableData.values()) {
            if (this.matchesConditions(item, conditions)) {
                results.push(item);
            }
        }
        return results;
    }
    
    async delete(table: string, key: Record<string, any>): Promise<void> {
        const tableData = this.tables.get(table);
        if (tableData) {
            tableData.delete(JSON.stringify(key));
        }
    }
    
    private matchesConditions(item: any, conditions: QueryCondition[]): boolean {
        return conditions.every(condition => {
            const value = item[condition.field];
            switch (condition.operator) {
                case 'eq': return value === condition.value;
                case 'gt': return value > condition.value;
                case 'lt': return value < condition.value;
                case 'contains': return value.includes(condition.value);
                default: return false;
            }
        });
    }
}

// Stub implementations for other adapters (würden real implementiert)
class AWSDynamoDBAdapter implements DatabaseService {
    constructor(private config: AWSConfig) {}
    async put(table: string, item: Record<string, any>): Promise<void> { /* AWS DynamoDB implementation */ }
    async get(table: string, key: Record<string, any>): Promise<any> { /* AWS DynamoDB implementation */ }
    async query(table: string, conditions: QueryCondition[]): Promise<any[]> { /* AWS DynamoDB implementation */ }
    async delete(table: string, key: Record<string, any>): Promise<void> { /* AWS DynamoDB implementation */ }
}

class AzureCosmosDBAdapter implements DatabaseService {
    constructor(private config: AzureConfig) {}
    async put(table: string, item: Record<string, any>): Promise<void> { /* Azure Cosmos DB implementation */ }
    async get(table: string, key: Record<string, any>): Promise<any> { /* Azure Cosmos DB implementation */ }
    async query(table: string, conditions: QueryCondition[]): Promise<any[]> { /* Azure Cosmos DB implementation */ }
    async delete(table: string, key: Record<string, any>): Promise<void> { /* Azure Cosmos DB implementation */ }
}

class GCPFirestoreAdapter implements DatabaseService {
    constructor(private config: GCPConfig) {}
    async put(table: string, item: Record<string, any>): Promise<void> { /* GCP Firestore implementation */ }
    async get(table: string, key: Record<string, any>): Promise<any> { /* GCP Firestore implementation */ }
    async query(table: string, conditions: QueryCondition[]): Promise<any[]> { /* GCP Firestore implementation */ }
    async delete(table: string, key: Record<string, any>): Promise<void> { /* GCP Firestore implementation */ }
}

// ============================================================================
// Main - Zeigt die Lösung
// ============================================================================

async function main() {
    console.log("Provider-agnostische Cloud-Architektur");
    console.log("=====================================");
    
    // Konfiguration kann einfach geändert werden
    const config: CloudConfig = {
        provider: 'aws', // Kann zu 'azure', 'gcp', oder 'local' geändert werden
        aws: {
            region: 'us-east-1',
            bucketName: 'my-bucket',
            tableName: 'my-table'
        },
        azure: {
            connectionString: 'DefaultEndpointsProtocol=https;...',
            containerName: 'my-container'
        },
        gcp: {
            projectId: 'my-project',
            keyFile: './key.json',
            bucketName: 'my-bucket'
        }
    };
    
    // Services werden über Factory erstellt
    const storage = CloudServiceFactory.createStorageService(config);
    const database = CloudServiceFactory.createDatabaseService(config);
    
    // Business Logic bleibt unverändert bei Provider-Wechsel
    const userService = new UserService(
        storage,
        database,
        null as any, // auth service
        null as any  // messaging service
    );
    
    console.log("\nVorteile:");
    console.log("1. Provider-unabhängige Business Logic");
    console.log("2. Einfache Migration zwischen Cloud-Providern");
    console.log("3. Testbarkeit mit lokalen Implementierungen");
    console.log("4. Keine Vendor Lock-in");
    console.log("5. Flexibilität bei Provider-Wahl");
    console.log("6. Multi-Cloud-Strategien möglich");
}

// VORTEILE:
// - Hexagonal Architecture / Ports & Adapters
// - Provider-Abstraktion
// - Dependency Inversion Principle
// - Einfache Migration
// - Testbarkeit
// - Flexibilität