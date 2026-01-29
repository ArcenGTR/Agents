# Frequently Asked Questions

## General Questions

### Q: What is included in the free tier?

**A:** The free tier includes:
- Up to 1,000 API calls per hour
- Basic documentation
- Community support
- Standard performance SLA (99.5%)

See pricing page for detailed comparison.

### Q: Can I upgrade from free to paid?

**A:** Yes, you can upgrade at any time:
1. Log in to your account
2. Go to Settings → Billing
3. Click "Upgrade Plan"
4. Choose your plan
5. Billing is prorated

No downtime during upgrade.

### Q: How do I change my plan?

**A:** To change your plan:
1. Log in to your account
2. Go to Settings → Billing → Current Plan
3. Click "Change Plan"
4. Select new plan
5. Confirm changes

Changes take effect immediately.

## API Questions

### Q: How do I get my API key?

**A:** To generate an API key:
1. Log in to https://app.example.com
2. Go to Settings → API Keys
3. Click "Generate New Key"
4. Copy the key immediately (not shown again)
5. Store securely

Never commit API keys to version control.

### Q: What if I lose my API key?

**A:** If you lose your API key:
1. You cannot recover it
2. Delete the old key in Settings
3. Generate a new key
4. Update your application with new key

**Recommendation**: Store keys in environment variables or secret management system.

### Q: Can I have multiple API keys?

**A:** Yes, you can create multiple API keys:
- One for development, one for production
- One per application
- Different keys can have different scopes

This is a security best practice.

### Q: How often should I rotate my API keys?

**A:** Industry best practice:
- Rotate keys every 90 days
- Immediately if compromised
- When team members leave

We recommend quarterly rotation.

## Rate Limiting Questions

### Q: Why did I get rate limited?

**A:** You exceed your plan's limits:
- Free: 1,000 requests/hour
- Professional: 10,000 requests/hour
- Enterprise: Custom limits

Check your dashboard to see current usage.

### Q: How do I avoid rate limiting?

**A:** Best practices:
- Use pagination for list endpoints
- Implement caching (5-10 minutes)
- Batch operations when possible
- Use webhooks instead of polling
- Upgrade to higher tier if needed

### Q: What happens when I'm rate limited?

**A:** When rate limited:
- Get 429 status code
- Response includes X-RateLimit-Reset header
- Should implement exponential backoff
- Retry after wait period

### Q: Can I request higher rate limits?

**A:** For Enterprise customers:
- Contact sales@example.com
- Provide expected usage patterns
- Custom limits can be arranged

## Integration Questions

### Q: What languages are supported?

**A:** Our API works with any language supporting HTTP/REST:
- Python
- JavaScript/Node.js
- Java
- Go
- PHP
- Ruby
- C#
- And more!

We provide SDK examples in documentation.

### Q: Do you provide SDKs?

**A:** Official SDKs available for:
- Python: `pip install example-sdk`
- JavaScript: `npm install @example/sdk`
- Java: Maven dependency

Community SDKs available for other languages.

### Q: Can I use the API in my mobile app?

**A:** Yes, but with security considerations:
- Never embed API key in app
- Use OAuth or token-based auth
- Backend should validate requests
- Recommended: Use your own backend as proxy

Contact us for mobile implementation guide.

### Q: How do I integrate webhooks?

**A:** To set up webhooks:
1. Configure webhook URL in dashboard
2. Select events to receive
3. Implement endpoint that handles webhook payloads
4. Verify webhook signature (for security)

Full webhook guide in API documentation.

## Security Questions

### Q: Is my data encrypted?

**A:** Yes, comprehensive encryption:
- In transit: HTTPS/TLS 1.3
- At rest: AES-256 encryption
- All API communications encrypted
- Database encryption enabled

See security documentation for details.

### Q: Who can access my data?

**A:** Data access controls:
- Only you and your team members
- Team owners can grant permissions
- API keys are user-specific
- Audit logs track all access

No other customers can see your data.

### Q: How do you handle PII (Personally Identifiable Information)?

**A:** PII handling:
- Minimized collection
- Encrypted storage
- Automatic deletion on account deletion
- GDPR compliant
- CCPA compliant

See privacy policy for details.

### Q: What security certifications do you have?

**A:** Our security:
- SOC 2 Type II certified
- ISO 27001 compliant
- GDPR compliant
- CCPA compliant
- Regular penetration testing

View security documentation for full details.

### Q: What if I think there's a security vulnerability?

**A:** If you discover a vulnerability:
- Email security@example.com immediately
- Don't post publicly
- We offer bug bounty program
- Fast response and patch timeline

Do NOT test vulnerabilities on production.

## Performance Questions

### Q: How fast is the API?

**A:** Typical response times:
- 50% of requests: <100ms
- 95% of requests: <500ms
- 99% of requests: <1000ms

Varies based on operation complexity.

### Q: Will my API calls slow down during peak usage?

**A:** Performance:
- Dedicated infrastructure
- Auto-scaling capabilities
- 99.9% uptime SLA
- Load balancing
- Performance may vary <5% at peak

Enterprise customers get priority.

### Q: How can I optimize my API usage?

**A:** Optimization techniques:
- Use pagination (limit results)
- Cache results locally
- Batch operations
- Use webhooks instead of polling
- Use specific filters in queries

See performance guide for details.

## Billing Questions

### Q: When do I get billed?

**A:** Billing details:
- Monthly billing (default)
- Billed on the same day each month
- For upgrades: prorated billing
- Invoice sent automatically

You can export invoices from dashboard.

### Q: What payment methods do you accept?

**A:** Payment methods:
- Credit card (Visa, Mastercard, Amex)
- Wire transfer (Enterprise)
- ACH transfer (US)

All major payment methods supported.

### Q: Can I get a refund?

**A:** Refund policy:
- 30-day money-back guarantee
- Full refund if unsatisfied
- Pro-rated refunds for plan changes
- Contact support for refund request

Email: support@example.com

### Q: Do you offer yearly plans with discount?

**A:** Yes, yearly plans:
- 15-20% discount vs monthly
- Pay upfront for 12 months
- Activate in Settings → Billing

Contact sales for enterprise yearly rates.

## Support Questions

### Q: How do I get support?

**A:** Support channels:
- Email: support@example.com
- Chat: https://support.example.com/chat
- Phone: For Enterprise customers
- Community: https://community.example.com

Response times vary by plan.

### Q: What are response time SLAs?

**A:** Support SLAs:
- Free: Best effort (24-48 hours)
- Professional: 24 hours response
- Enterprise: 4 hours response

24/7 support for critical issues.

### Q: Is there a knowledge base?

**A:** Yes, documentation:
- API documentation: https://docs.example.com
- Tutorial: https://docs.example.com/tutorial
- Community Q&A: https://community.example.com
- Status page: https://status.example.com

Searchable knowledge base available.

### Q: Can I request a feature?

**A:** Feature requests:
- Vote on existing requests
- Submit new requests via feedback portal
- Share use cases
- Enterprise customers get priority

Visit https://feature-requests.example.com

## Account & Subscription Questions

### Q: Can I delete my account?

**A:** To delete your account:
1. Email support@example.com
2. Confirm your request
3. Account data deleted
4. Billing stopped immediately

Cannot recover data after deletion.

### Q: Can I transfer my data to another account?

**A:** Data transfer:
- We provide data export (JSON format)
- You can transfer to another account
- Contact support@example.com for export

Export completed within 5 business days.

### Q: What happens if I don't pay my bill?

**A:** Non-payment policy:
- 7-day grace period
- Service suspended after grace period
- Data retained for 30 days
- Can reactivate by paying past due amount

Auto-retry on file after 3 days.

### Q: Can I have multiple accounts?

**A:** Multiple accounts:
- Free tier: 1 account per email
- Paid tier: 1 primary account per email
- Team accounts available
- Enterprise: Contact sales

Separate accounts require separate emails.

## Technical Questions

### Q: What's the maximum request size?

**A:** Request limits:
- Maximum body size: 10 MB
- Maximum URL length: 8000 characters
- Maximum header size: 8 KB

Contact support for larger files.

### Q: What versions are supported?

**A:** Version support:
- Current version: v1 (supported)
- Previous version: v0 (deprecated, 12-month notice)
- Deprecation notice: 12 months before removal
- Migration guide provided

Always use latest version.

### Q: Can I use the API offline?

**A:** API is cloud-based:
- Requires internet connection
- Cannot use offline
- For offline capability, request enterprise solution
- Self-hosted option available

Contact sales for self-hosted version.

## Getting More Help

**Still have questions?**

- Check the full documentation: https://docs.example.com
- Search community questions: https://community.example.com
- Contact support: support@example.com
- Schedule demo: https://calendly.com/example/demo