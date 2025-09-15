// BAD EXAMPLE: Vendor Lock-in Anti-Pattern
// Starke Abhängigkeit von AWS-spezifischen Services ohne Abstraktion.
// Migration zu einem anderen Cloud-Provider wäre extrem aufwändig.

import { 
    S3, 
    DynamoDB, 
    Lambda, 
    SQS, 
    SNS, 
    Cognito,
    CloudWatch,
    Kinesis,
    Rekognition,
    Comprehend
} from 'aws-sdk';

// ============================================================================
// PROBLEM: Direkte AWS SDK Verwendung überall im Code
// ============================================================================

class UserService {
    private s3: S3;
    private dynamodb: DynamoDB;
    private cognito: Cognito;
    private sns: SNS;
    
    constructor() {
        // Direkte AWS Service Initialisierung
        this.s3 = new S3({ region: 'us-east-1' });
        this.dynamodb = new DynamoDB({ region: 'us-east-1' });
        this.cognito = new Cognito({ region: 'us-east-1' });
        this.sns = new SNS({ region: 'us-east-1' });
    }
    
    async createUser(email: string, password: string) {
        // AWS Cognito-spezifische User-Erstellung
        const cognitoUser = await this.cognito.adminCreateUser({
            UserPoolId: 'us-east-1_xxxxx',
            Username: email,
            MessageAction: 'SUPPRESS',
            UserAttributes: [
                { Name: 'email', Value: email },
                { Name: 'email_verified', Value: 'true' }
            ]
        }).promise();
        
        // AWS DynamoDB-spezifische Datenspeicherung
        await this.dynamodb.putItem({
            TableName: 'users-table',
            Item: {
                userId: { S: cognitoUser.User!.Username! },
                email: { S: email },
                createdAt: { N: Date.now().toString() }
            }
        }).promise();
        
        // AWS SNS-spezifische Benachrichtigung
        await this.sns.publish({
            TopicArn: 'arn:aws:sns:us-east-1:123456789:user-events',
            Message: JSON.stringify({ event: 'user.created', userId: cognitoUser.User!.Username })
        }).promise();
        
        return cognitoUser;
    }
    
    async uploadProfilePicture(userId: string, imageBuffer: Buffer) {
        // AWS S3-spezifischer Upload
        const result = await this.s3.upload({
            Bucket: 'user-profile-pictures',
            Key: `${userId}/profile.jpg`,
            Body: imageBuffer,
            ContentType: 'image/jpeg',
            ServerSideEncryption: 'AES256'
        }).promise();
        
        return result.Location;
    }
}

// ============================================================================
// PROBLEM: AWS Lambda-spezifische Handler
// ============================================================================

export const processOrderHandler = async (event: any, context: any) => {
    // AWS Lambda-spezifisches Event Format
    const sqs = new SQS();
    const dynamodb = new DynamoDB();
    
    for (const record of event.Records) {
        const order = JSON.parse(record.body);
        
        // AWS DynamoDB Streams-spezifische Verarbeitung
        await dynamodb.putItem({
            TableName: 'orders-table',
            Item: {
                orderId: { S: order.id },
                customerId: { S: order.customerId },
                amount: { N: order.amount.toString() },
                status: { S: 'processing' }
            }
        }).promise();
        
        // AWS SQS-spezifische Message-Verarbeitung
        await sqs.sendMessage({
            QueueUrl: 'https://sqs.us-east-1.amazonaws.com/123456789/order-processing',
            MessageBody: JSON.stringify(order),
            MessageAttributes: {
                'MessageType': { DataType: 'String', StringValue: 'Order' }
            }
        }).promise();
    }
    
    return {
        statusCode: 200,
        body: JSON.stringify({ processed: event.Records.length })
    };
};

// ============================================================================
// PROBLEM: AWS CloudWatch-spezifisches Monitoring
// ============================================================================

class MonitoringService {
    private cloudwatch: CloudWatch;
    
    constructor() {
        this.cloudwatch = new CloudWatch({ region: 'us-east-1' });
    }
    
    async logMetric(metricName: string, value: number) {
        // AWS CloudWatch-spezifische Metriken
        await this.cloudwatch.putMetricData({
            Namespace: 'MyApplication',
            MetricData: [
                {
                    MetricName: metricName,
                    Value: value,
                    Unit: 'Count',
                    Timestamp: new Date()
                }
            ]
        }).promise();
    }
    
    async createAlarm(alarmName: string, metricName: string) {
        // AWS CloudWatch-spezifische Alarme
        await this.cloudwatch.putMetricAlarm({
            AlarmName: alarmName,
            ComparisonOperator: 'GreaterThanThreshold',
            EvaluationPeriods: 1,
            MetricName: metricName,
            Namespace: 'MyApplication',
            Period: 300,
            Statistic: 'Average',
            Threshold: 100,
            ActionsEnabled: true,
            AlarmActions: ['arn:aws:sns:us-east-1:123456789:alerts']
        }).promise();
    }
}

// ============================================================================
// PROBLEM: AWS Kinesis-spezifisches Event Streaming
// ============================================================================

class EventStreamService {
    private kinesis: Kinesis;
    
    constructor() {
        this.kinesis = new Kinesis({ region: 'us-east-1' });
    }
    
    async publishEvent(eventType: string, data: any) {
        // AWS Kinesis-spezifisches Streaming
        await this.kinesis.putRecord({
            StreamName: 'event-stream',
            Data: JSON.stringify({ eventType, data, timestamp: Date.now() }),
            PartitionKey: eventType
        }).promise();
    }
    
    async consumeEvents(streamName: string) {
        // AWS Kinesis-spezifischer Consumer
        const response = await this.kinesis.describeStream({
            StreamName: streamName
        }).promise();
        
        for (const shard of response.StreamDescription.Shards) {
            const iterator = await this.kinesis.getShardIterator({
                StreamName: streamName,
                ShardId: shard.ShardId,
                ShardIteratorType: 'TRIM_HORIZON'
            }).promise();
            
            const records = await this.kinesis.getRecords({
                ShardIterator: iterator.ShardIterator!
            }).promise();
            
            // Verarbeitung...
        }
    }
}

// ============================================================================
// PROBLEM: AWS AI Services direkte Verwendung
// ============================================================================

class AIService {
    private rekognition: Rekognition;
    private comprehend: Comprehend;
    
    constructor() {
        this.rekognition = new Rekognition({ region: 'us-east-1' });
        this.comprehend = new Comprehend({ region: 'us-east-1' });
    }
    
    async analyzeImage(imageBuffer: Buffer) {
        // AWS Rekognition-spezifische Bildanalyse
        const result = await this.rekognition.detectLabels({
            Image: { Bytes: imageBuffer },
            MaxLabels: 10,
            MinConfidence: 70
        }).promise();
        
        return result.Labels;
    }
    
    async analyzeSentiment(text: string) {
        // AWS Comprehend-spezifische Textanalyse
        const result = await this.comprehend.detectSentiment({
            Text: text,
            LanguageCode: 'en'
        }).promise();
        
        return result.Sentiment;
    }
}

// ============================================================================
// PROBLEM: AWS-spezifische Konfiguration
// ============================================================================

const config = {
    aws: {
        region: 'us-east-1',
        credentials: {
            accessKeyId: process.env.AWS_ACCESS_KEY_ID!,
            secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY!
        },
        services: {
            s3: { bucket: 'my-app-bucket' },
            dynamodb: { 
                tables: {
                    users: 'users-table',
                    orders: 'orders-table',
                    products: 'products-table'
                }
            },
            cognito: { userPoolId: 'us-east-1_xxxxx' },
            sqs: { 
                queues: {
                    orders: 'https://sqs.us-east-1.amazonaws.com/123456789/orders',
                    notifications: 'https://sqs.us-east-1.amazonaws.com/123456789/notifications'
                }
            }
        }
    }
};

// ============================================================================
// Main - Zeigt die Probleme
// ============================================================================

async function main() {
    console.log("Vendor Lock-in Anti-Pattern: AWS-spezifische Implementierung");
    console.log("==========================================================");
    
    const userService = new UserService();
    const monitoringService = new MonitoringService();
    const eventService = new EventStreamService();
    const aiService = new AIService();
    
    // Jede Operation ist AWS-spezifisch
    // Migration zu Azure, GCP oder On-Premise wäre ein komplettes Rewrite
    
    console.log("\nProbleme:");
    console.log("1. Direkte AWS SDK Verwendung überall");
    console.log("2. AWS-spezifische Event-Formate");
    console.log("3. AWS-spezifische Konfiguration");
    console.log("4. Keine Abstraktion der Cloud-Services");
    console.log("5. Migration würde komplettes Rewrite erfordern");
}

// PROBLEME:
// - Starke Kopplung an AWS
// - Keine Portabilität
// - Vendor Lock-in
// - Hohe Migrationskosten
// - Keine Flexibilität bei Provider-Wahl
// - Abhängigkeit von proprietären Services