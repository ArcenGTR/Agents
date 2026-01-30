Multi-Agent Support System

- **Technical Specialist** - Documentation-driven support
- **Billing Specialist** - Tool-enabled support


#### 1. **get_plan_info** - Retrieve Customer Plan Details

#### 2. **send_refund_form** - Send Refund Request Form

#### 3. **open_support_case** - Escalate to Human Support


### Build & Run with Docker

#### 1. Paste your OpenAI API key into `.env`

#### 2. Execute commands bellow:

```bash

docker compose build

docker compose up -d && docker attach support_agent_system
```

### Console Commands

Once running, use these commands in the interactive console:

```
Your message                    → Send a message to the system
exit                           → Exit the application
history                        → Show full conversation history
clear                          → Clear conversation history
```