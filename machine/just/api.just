# Get coffee machine status
status:
    @curl -s {{machine-url}}/status | jq

# Submit coffee order
[arg("type", help="coffee type")]
order type='AMERICANO':
    @curl \
        -s \
        -o /dev/null \
        -w "HTTP %{http_code}\n" \
        -X POST \
        -H "Content-Type: application/json" \
        -d '{"type":"{{type}}"}' \
        {{machine-url}}/order

# Get brewing progress
progress:
    @curl -s {{machine-url}}/progress | jq

# Watch brewing progress until complete
watch-progress:
    @watch -n 1 "curl -s {{machine-url}}/progress | jq"