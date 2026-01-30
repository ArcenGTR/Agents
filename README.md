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

Some starting logs are not visible, but after running you can freely chat with multi-agent system.

Logs are visible in console in order, to have insights on how system internally works.

### Console Commands

Once running, use these commands in the interactive console:

```
Your message                    → Send a message to the system
exit                           → Exit the application
history                        → Show full conversation history
clear                          → Clear conversation history
```