// BAD EXAMPLE: Golden Hammer Anti-Pattern
// Alles wird als REST API implementiert, auch wenn es unpassend ist.
// Real-time Chat System als REST API statt WebSockets.
// Message Queue als REST API statt echtem Message Broker.
// File Streaming als REST API statt Binary Protocol.

use std::collections::HashMap;
use std::sync::{Arc, Mutex};
use std::time::{Duration, SystemTime, UNIX_EPOCH};
use std::thread;
use serde::{Deserialize, Serialize};

// ============================================================================
// PROBLEM 1: Real-time Chat als REST API (sollte WebSocket sein)
// ============================================================================

#[derive(Clone, Debug, Serialize, Deserialize)]
struct ChatMessage {
    id: u64,
    user_id: String,
    channel: String,
    content: String,
    timestamp: u64,
}

// Chat Service - verwendet REST Polling statt WebSockets
struct ChatServiceBad {
    messages: Arc<Mutex<Vec<ChatMessage>>>,
    last_message_id: Arc<Mutex<u64>>,
}

impl ChatServiceBad {
    fn new() -> Self {
        ChatServiceBad {
            messages: Arc::new(Mutex::new(Vec::new())),
            last_message_id: Arc::new(Mutex::new(0)),
        }
    }

    // POST /api/chat/send - Message senden
    fn send_message(&self, user_id: String, channel: String, content: String) -> String {
        let mut messages = self.messages.lock().unwrap();
        let mut last_id = self.last_message_id.lock().unwrap();
        
        *last_id += 1;
        let message = ChatMessage {
            id: *last_id,
            user_id,
            channel,
            content,
            timestamp: SystemTime::now()
                .duration_since(UNIX_EPOCH)
                .unwrap()
                .as_secs(),
        };
        
        messages.push(message.clone());
        
        // REST Response
        serde_json::to_string(&message).unwrap()
    }

    // GET /api/chat/poll?last_id=123&timeout=30 - Long Polling für neue Nachrichten
    // PROBLEM: Ineffizient, hohe Latenz, Server-Ressourcen verschwendet
    fn poll_messages(&self, last_id: u64, channel: String, timeout_secs: u64) -> String {
        let start_time = SystemTime::now();
        let timeout = Duration::from_secs(timeout_secs);
        
        // Busy waiting - verschwendet CPU!
        loop {
            let messages = self.messages.lock().unwrap();
            let new_messages: Vec<ChatMessage> = messages
                .iter()
                .filter(|m| m.id > last_id && m.channel == channel)
                .cloned()
                .collect();
            
            if !new_messages.is_empty() {
                return serde_json::to_string(&new_messages).unwrap();
            }
            
            // Check timeout
            if SystemTime::now().duration_since(start_time).unwrap() > timeout {
                // Timeout - return empty array
                return "[]".to_string();
            }
            
            // Drop lock and sleep - ineffizient!
            drop(messages);
            thread::sleep(Duration::from_millis(100));
        }
    }

    // GET /api/chat/history?channel=general&limit=50 - Message History
    fn get_history(&self, channel: String, limit: usize) -> String {
        let messages = self.messages.lock().unwrap();
        let history: Vec<ChatMessage> = messages
            .iter()
            .filter(|m| m.channel == channel)
            .rev()
            .take(limit)
            .cloned()
            .collect();
        
        serde_json::to_string(&history).unwrap()
    }
}

// ============================================================================
// PROBLEM 2: Message Queue als REST API (sollte echte Queue sein)
// ============================================================================

#[derive(Clone, Debug, Serialize, Deserialize)]
struct QueueMessage {
    id: String,
    payload: String,
    created_at: u64,
    attempts: u32,
}

// Queue Service - REST API statt echtem Message Broker
struct QueueServiceBad {
    queues: Arc<Mutex<HashMap<String, Vec<QueueMessage>>>>,
}

impl QueueServiceBad {
    fn new() -> Self {
        QueueServiceBad {
            queues: Arc::new(Mutex::new(HashMap::new())),
        }
    }

    // POST /api/queue/{queue_name}/publish - Message publizieren
    fn publish(&self, queue_name: String, payload: String) -> String {
        let mut queues = self.queues.lock().unwrap();
        let queue = queues.entry(queue_name).or_insert_with(Vec::new);
        
        let message = QueueMessage {
            id: format!("msg_{}", SystemTime::now()
                .duration_since(UNIX_EPOCH)
                .unwrap()
                .as_nanos()),
            payload,
            created_at: SystemTime::now()
                .duration_since(UNIX_EPOCH)
                .unwrap()
                .as_secs(),
            attempts: 0,
        };
        
        queue.push(message.clone());
        
        // REST Response
        format!("{{\"message_id\": \"{}\"}}", message.id)
    }

    // GET /api/queue/{queue_name}/consume - Message konsumieren
    // PROBLEM: Kein echtes Queue-Verhalten, Race Conditions, keine Garantien
    fn consume(&self, queue_name: String) -> String {
        let mut queues = self.queues.lock().unwrap();
        
        if let Some(queue) = queues.get_mut(&queue_name) {
            if !queue.is_empty() {
                // Einfaches FIFO - keine Prioritäten, kein Retry-Handling
                let message = queue.remove(0);
                return serde_json::to_string(&message).unwrap();
            }
        }
        
        // Keine Message verfügbar
        "null".to_string()
    }

    // POST /api/queue/{queue_name}/ack/{message_id} - Message bestätigen
    fn acknowledge(&self, queue_name: String, message_id: String) -> String {
        // In echter Queue würde dies die Message als verarbeitet markieren
        // Hier macht es nichts, da Message bereits entfernt wurde
        format!("{{\"status\": \"acknowledged\", \"message_id\": \"{}\"}}", message_id)
    }
}

// ============================================================================
// PROBLEM 3: File Streaming als REST API (sollte Binary Protocol sein)
// ============================================================================

struct FileServiceBad {
    files: Arc<Mutex<HashMap<String, Vec<u8>>>>,
}

impl FileServiceBad {
    fn new() -> Self {
        FileServiceBad {
            files: Arc::new(Mutex::new(HashMap::new())),
        }
    }

    // POST /api/files/upload - File Upload als Base64 in JSON
    // PROBLEM: 33% Overhead durch Base64, Memory-intensive
    fn upload_file(&self, filename: String, content_base64: String) -> String {
        let decoded = base64::decode(&content_base64).unwrap_or_default();
        
        let mut files = self.files.lock().unwrap();
        files.insert(filename.clone(), decoded);
        
        format!("{{\"filename\": \"{}\", \"size\": {}}}", 
                filename, content_base64.len())
    }

    // GET /api/files/download/{filename} - File Download als Base64 in JSON
    // PROBLEM: Gesamte Datei im Memory, Base64 Overhead
    fn download_file(&self, filename: String) -> String {
        let files = self.files.lock().unwrap();
        
        if let Some(content) = files.get(&filename) {
            let encoded = base64::encode(content);
            format!("{{\"filename\": \"{}\", \"content\": \"{}\"}}", 
                    filename, encoded)
        } else {
            "{{\"error\": \"File not found\"}}".to_string()
        }
    }

    // GET /api/files/stream/{filename}?chunk=1&size=1024 - Chunked Download
    // PROBLEM: Jeder Chunk ist ein separater HTTP Request!
    fn stream_file(&self, filename: String, chunk_number: usize, chunk_size: usize) -> String {
        let files = self.files.lock().unwrap();
        
        if let Some(content) = files.get(&filename) {
            let start = chunk_number * chunk_size;
            let end = std::cmp::min(start + chunk_size, content.len());
            
            if start < content.len() {
                let chunk = &content[start..end];
                let encoded = base64::encode(chunk);
                
                return format!(
                    "{{\"chunk\": {}, \"data\": \"{}\", \"total_chunks\": {}}}",
                    chunk_number,
                    encoded,
                    (content.len() + chunk_size - 1) / chunk_size
                );
            }
        }
        
        "{{\"error\": \"Invalid chunk\"}}".to_string()
    }
}

// ============================================================================
// PROBLEM 4: Database Transactions als REST API
// ============================================================================

struct TransactionServiceBad {
    transactions: Arc<Mutex<HashMap<String, Vec<String>>>>,
}

impl TransactionServiceBad {
    fn new() -> Self {
        TransactionServiceBad {
            transactions: Arc::new(Mutex::new(HashMap::new())),
        }
    }

    // POST /api/transaction/begin - Transaction starten
    // PROBLEM: Stateful REST API, Session Management Problem
    fn begin_transaction(&self) -> String {
        let tx_id = format!("tx_{}", SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_nanos());
        
        let mut transactions = self.transactions.lock().unwrap();
        transactions.insert(tx_id.clone(), Vec::new());
        
        format!("{{\"transaction_id\": \"{}\"}}", tx_id)
    }

    // POST /api/transaction/{tx_id}/execute - SQL ausführen
    // PROBLEM: Multiple Round-trips, keine echte Transaktions-Isolation
    fn execute_sql(&self, tx_id: String, sql: String) -> String {
        let mut transactions = self.transactions.lock().unwrap();
        
        if let Some(tx) = transactions.get_mut(&tx_id) {
            tx.push(sql);
            format!("{{\"status\": \"queued\", \"transaction_id\": \"{}\"}}", tx_id)
        } else {
            "{{\"error\": \"Transaction not found\"}}".to_string()
        }
    }

    // POST /api/transaction/{tx_id}/commit - Transaction committen
    fn commit_transaction(&self, tx_id: String) -> String {
        let mut transactions = self.transactions.lock().unwrap();
        
        if let Some(queries) = transactions.remove(&tx_id) {
            // Würde hier alle Queries ausführen
            format!("{{\"status\": \"committed\", \"queries_executed\": {}}}", 
                    queries.len())
        } else {
            "{{\"error\": \"Transaction not found\"}}".to_string()
        }
    }
}

// ============================================================================
// PROBLEM 5: Event Streaming als REST API
// ============================================================================

#[derive(Clone, Debug, Serialize, Deserialize)]
struct Event {
    id: u64,
    event_type: String,
    data: String,
    timestamp: u64,
}

struct EventStreamBad {
    events: Arc<Mutex<Vec<Event>>>,
    last_event_id: Arc<Mutex<u64>>,
}

impl EventStreamBad {
    fn new() -> Self {
        EventStreamBad {
            events: Arc::new(Mutex::new(Vec::new())),
            last_event_id: Arc::new(Mutex::new(0)),
        }
    }

    // POST /api/events/publish - Event publizieren
    fn publish_event(&self, event_type: String, data: String) -> String {
        let mut events = self.events.lock().unwrap();
        let mut last_id = self.last_event_id.lock().unwrap();
        
        *last_id += 1;
        let event = Event {
            id: *last_id,
            event_type,
            data,
            timestamp: SystemTime::now()
                .duration_since(UNIX_EPOCH)
                .unwrap()
                .as_secs(),
        };
        
        events.push(event.clone());
        
        serde_json::to_string(&event).unwrap()
    }

    // GET /api/events/stream?last_id=0 - Event Stream via Polling
    // PROBLEM: Sollte Server-Sent Events oder WebSockets verwenden
    fn poll_events(&self, last_id: u64) -> String {
        // Wieder Polling statt echtem Streaming
        let events = self.events.lock().unwrap();
        let new_events: Vec<Event> = events
            .iter()
            .filter(|e| e.id > last_id)
            .cloned()
            .collect();
        
        serde_json::to_string(&new_events).unwrap()
    }
}

// ============================================================================
// PROBLEM 6: Distributed Lock als REST API
// ============================================================================

struct LockServiceBad {
    locks: Arc<Mutex<HashMap<String, String>>>,
}

impl LockServiceBad {
    fn new() -> Self {
        LockServiceBad {
            locks: Arc::new(Mutex::new(HashMap::new())),
        }
    }

    // POST /api/lock/{resource}/acquire?owner=client1&timeout=30
    // PROBLEM: Network Latency, keine echte Atomarität
    fn acquire_lock(&self, resource: String, owner: String, _timeout: u64) -> String {
        let mut locks = self.locks.lock().unwrap();
        
        if locks.contains_key(&resource) {
            "{{\"status\": \"locked\", \"acquired\": false}}".to_string()
        } else {
            locks.insert(resource.clone(), owner.clone());
            format!("{{\"status\": \"acquired\", \"resource\": \"{}\", \"owner\": \"{}\"}}", 
                    resource, owner)
        }
    }

    // POST /api/lock/{resource}/release?owner=client1
    fn release_lock(&self, resource: String, owner: String) -> String {
        let mut locks = self.locks.lock().unwrap();
        
        if let Some(lock_owner) = locks.get(&resource) {
            if lock_owner == &owner {
                locks.remove(&resource);
                format!("{{\"status\": \"released\", \"resource\": \"{}\"}}", resource)
            } else {
                "{{\"error\": \"Not the lock owner\"}}".to_string()
            }
        } else {
            "{{\"error\": \"Lock not found\"}}".to_string()
        }
    }
}

// ============================================================================
// Main - Zeigt alle Probleme
// ============================================================================

fn main() {
    println!("Golden Hammer Anti-Pattern: Alles als REST API");
    println!("================================================");
    
    // Problem 1: Chat System
    let chat = ChatServiceBad::new();
    println!("\n1. Chat System als REST (sollte WebSocket sein):");
    chat.send_message("user1".to_string(), "general".to_string(), "Hello!".to_string());
    
    // Client muss ständig pollen - ineffizient!
    thread::spawn(move || {
        loop {
            let _messages = chat.poll_messages(0, "general".to_string(), 30);
            // Process messages...
            thread::sleep(Duration::from_secs(1));
        }
    });
    
    // Problem 2: Message Queue
    let queue = QueueServiceBad::new();
    println!("\n2. Message Queue als REST (sollte echter Broker sein):");
    queue.publish("orders".to_string(), "Order #123".to_string());
    let _message = queue.consume("orders".to_string());
    // Keine Delivery Guarantees, keine Retries, etc.
    
    // Problem 3: File Streaming
    let files = FileServiceBad::new();
    println!("\n3. File Streaming als REST (sollte Binary Protocol sein):");
    let large_file = vec![0u8; 10_000_000]; // 10MB
    let encoded = base64::encode(&large_file); // 33% Overhead!
    files.upload_file("large.bin".to_string(), encoded);
    
    // Problem 4: Database Transactions
    let tx_service = TransactionServiceBad::new();
    println!("\n4. Database Transactions als REST:");
    let tx_id = tx_service.begin_transaction();
    tx_service.execute_sql(tx_id.clone(), "UPDATE users SET ...".to_string());
    tx_service.execute_sql(tx_id.clone(), "INSERT INTO logs ...".to_string());
    tx_service.commit_transaction(tx_id);
    // Multiple Round-trips, State Management Problem
    
    println!("\nAlle diese Use Cases sollten NICHT als REST API implementiert werden!");
    println!("Bessere Alternativen:");
    println!("- Chat: WebSockets");
    println!("- Message Queue: RabbitMQ, Kafka, Redis Pub/Sub");
    println!("- File Streaming: gRPC, Binary Protocols");
    println!("- Transactions: Direkter DB-Zugriff");
    println!("- Events: Server-Sent Events, WebSockets");
    println!("- Locks: Redis, Zookeeper, etcd");
}

// Helper module for base64
mod base64 {
    pub fn encode(data: &[u8]) -> String {
        // Simplified base64 encoding
        format!("base64_encoded_{}_bytes", data.len())
    }
    
    pub fn decode(_encoded: &str) -> Result<Vec<u8>, ()> {
        // Simplified decoding
        Ok(vec![0u8; 100])
    }
}