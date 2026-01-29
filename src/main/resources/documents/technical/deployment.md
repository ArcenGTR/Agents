# Deployment Guide

## Overview

This guide covers building, containerizing, and deploying the Multi-Agent Support System.

## Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher
- Docker (for containerization)
- kubectl (for Kubernetes deployment)
- OpenAI API key

## Local Development

### 1. Clone Repository
```bash
git clone https://github.com/example/multi-agent-support-system.git
cd multi-agent-support-system
```

### 2. Install Dependencies
```bash
# Using Maven
mvn clean install

# Or using IDE
# IntelliJ: File → Open → select pom.xml
# Eclipse: File → Import → Existing Maven Projects
# VS Code: Install Extension Pack for Java
```

### 3. Configure Environment
```bash
# Create .env file
cat > .env << EOF
OPENAI_API_KEY=sk-your-api-key-here
LOG_LEVEL=DEBUG
AGENT_MAX_TURNS=20
EOF

# Or set environment variable
export OPENAI_API_KEY=sk-your-api-key-here
```

### 4. Run Locally
```bash
# Option 1: Maven
mvn spring-boot:run

# Option 2: Run JAR
mvn package
java -jar target/multi-agent-support-system-1.0.0.jar

# Option 3: IDE Run Configuration
# IntelliJ: Right-click Application.java → Run
```

### 5. Test Locally
```bash
# In another terminal
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "How do I authenticate?"}'
```

## Docker Deployment

### Building Docker Image
```bash
# Build image
docker build -t support-ai:1.0 .

# Build with specific tag
docker build -t myregistry.azurecr.io/support-ai:latest .

# Verify image was created
docker images
```

### Running Container
```bash
# Run container
docker run -e OPENAI_API_KEY=sk-... \
           -p 8080:8080 \
           support-ai:1.0

# Run with volume mount
docker run -e OPENAI_API_KEY=sk-... \
           -p 8080:8080 \
           -v $(pwd)/documents:/app/documents \
           support-ai:1.0

# Run in background
docker run -d -e OPENAI_API_KEY=sk-... \
           -p 8080:8080 \
           --name support-ai \
           support-ai:1.0
```

### Docker Compose
```bash
# Start all services
docker-compose up

# Start in background
docker-compose up -d

# View logs
docker-compose logs -f support-ai

# Stop all services
docker-compose down
```

### Pushing to Registry
```bash
# Tag image
docker tag support-ai:1.0 myregistry.azurecr.io/support-ai:1.0

# Push to registry
docker push myregistry.azurecr.io/support-ai:1.0

# For Docker Hub
docker tag support-ai:1.0 username/support-ai:1.0
docker push username/support-ai:1.0
```

## Kubernetes Deployment

### Prerequisites
```bash
# Check kubectl is installed
kubectl version --client

# Verify cluster access
kubectl cluster-info

# Check nodes are ready
kubectl get nodes
```

### Create Namespace
```bash
# Create namespace for the application
kubectl create namespace support-system

# Set as default namespace
kubectl config set-context --current --namespace=support-system
```

### Create Secrets
```bash
# Create secret for API key
kubectl create secret generic openai-secret \
  --from-literal=api-key=sk-your-api-key \
  -n support-system

# Verify secret created
kubectl describe secret openai-secret -n support-system
```

### Deploy Application
```bash
# Apply deployment
kubectl apply -f deployment.yaml

# Apply service
kubectl apply -f service.yaml

# Check deployment status
kubectl get deployments
kubectl get pods
kubectl get services
```

### Verify Deployment
```bash
# Check pod status
kubectl get pods -o wide

# View logs
kubectl logs -f deployment/support-ai-agent

# Describe pod (for debugging)
kubectl describe pod <pod-name>

# Port forward (for local testing)
kubectl port-forward svc/support-ai-service 8080:80
```

### Scale Application
```bash
# Scale to 5 replicas
kubectl scale deployment support-ai-agent --replicas=5

# Auto-scaling
kubectl autoscale deployment support-ai-agent \
  --min=2 --max=10 --cpu-percent=80
```

### Update Application
```bash
# Update image
kubectl set image deployment/support-ai-agent \
  support-ai=myregistry.azurecr.io/support-ai:1.1

# Verify rollout
kubectl rollout status deployment/support-ai-agent

# Rollback if needed
kubectl rollout undo deployment/support-ai-agent
```

## Cloud Platform Deployment

### AWS ECS
```bash
# Create ECR repository
aws ecr create-repository --repository-name support-ai

# Push image
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin xxxxx.dkr.ecr.us-east-1.amazonaws.com

docker tag support-ai:1.0 xxxxx.dkr.ecr.us-east-1.amazonaws.com/support-ai:latest
docker push xxxxx.dkr.ecr.us-east-1.amazonaws.com/support-ai:latest

# Create ECS service with Fargate
# Use AWS Console or CDK
```

### Google Cloud Run
```bash
# Build and push
gcloud builds submit --tag gcr.io/PROJECT_ID/support-ai

# Deploy
gcloud run deploy support-ai \
  --image gcr.io/PROJECT_ID/support-ai \
  --platform managed \
  --region us-central1 \
  --set-env-vars OPENAI_API_KEY=sk-...
```

### Azure Container Instances
```bash
# Create container group
az container create \
  --resource-group myResourceGroup \
  --name support-ai \
  --image myregistry.azurecr.io/support-ai:latest \
  --environment-variables OPENAI_API_KEY=sk-... \
  --ports 8080 \
  --registry-login-server myregistry.azurecr.io \
  --registry-username username \
  --registry-password password
```

## Configuration Management

### Environment Variables
```bash
# Required
OPENAI_API_KEY=sk-...

# Optional (with defaults)
LOG_LEVEL=INFO
AGENT_MAX_TURNS=20
DATABASE_URL=postgresql://localhost/support
REDIS_URL=redis://localhost:6379
```

### ConfigMap (Kubernetes)
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  LOG_LEVEL: INFO
  AGENT_MAX_TURNS: "20"
```

### Secrets (Kubernetes)
```bash
# Create from literal
kubectl create secret generic app-secrets \
  --from-literal=openai-key=sk-...

# Create from file
kubectl create secret generic app-secrets \
  --from-file=config.yaml

# Create docker registry secret
kubectl create secret docker-registry regcred \
  --docker-server=myregistry.azurecr.io \
  --docker-username=username \
  --docker-password=password
```

## Health Checks & Monitoring

### Health Check Endpoint
```bash
# Check application health
curl http://localhost:8080/health

# Response
{
  "status": "UP",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### Metrics Endpoint
```bash
# Get metrics
curl http://localhost:8080/metrics

# Specific metric
curl http://localhost:8080/metrics/active_conversations
```

### Liveness & Readiness Probes (Kubernetes)
```yaml
livenessProbe:
  httpGet:
    path: /health
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /ready
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
```

## Logging & Monitoring

### View Logs
```bash
# Local
tail -f logs/application.log

# Docker
docker logs -f support-ai

# Kubernetes
kubectl logs -f pod/support-ai-agent-xxxxx

# Follow all pods in deployment
kubectl logs -f deployment/support-ai-agent
```

### Configure Logging Level
```bash
# Set at startup
java -Dlogging.level.com.support.ai=DEBUG -jar app.jar

# Via environment variable
export LOG_LEVEL=DEBUG
java -jar app.jar
```

## Troubleshooting

### Application won't start
```bash
# Check logs
docker logs support-ai

# Check configuration
echo $OPENAI_API_KEY

# Verify dependencies
mvn dependency:tree
```

### Container won't run
```bash
# Check image exists
docker images

# Check container logs
docker logs <container-id>

# Test locally first
docker run -it --entrypoint /bin/sh myimage
```

### Pod won't schedule
```bash
# Check resources
kubectl top nodes
kubectl describe node <node-name>

# Check resource requests
kubectl describe pod <pod-name>
```

## Production Checklist

- [ ] API keys configured securely
- [ ] HTTPS enabled
- [ ] Rate limiting configured
- [ ] Database backups enabled
- [ ] Monitoring/alerting setup
- [ ] Log aggregation configured
- [ ] Health checks working
- [ ] Auto-scaling configured
- [ ] Load testing completed
- [ ] Security audit passed
- [ ] Documentation updated
- [ ] Team trained
- [ ] Runbooks created
- [ ] Incident response plan ready

## Rollback Procedure
```bash
# Kubernetes
kubectl rollout undo deployment/support-ai-agent

# Verify rollback
kubectl rollout status deployment/support-ai-agent

# Docker Compose
docker-compose down
docker-compose up -d  # with previous version
```

## Support

For deployment issues:
- Check logs first
- Verify configuration
- Test with local setup
- Contact support with logs and error message