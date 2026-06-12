# Get coffee machine status
machine-status:
    @curl -s {{machine-url}}/status | jq

# Submit coffee order
[arg("type", help="coffee type")]
machine-order type:
    @curl \
        -s \
        -o /dev/null \
        -w "HTTP %{http_code}\n" \
        -X POST \
        -H "Content-Type: application/json" \
        -d '{"type":"{{type}}"}' \
        {{machine-url}}/order

# Get brewing progress
machine-progress:
    @curl -s {{machine-url}}/progress | jq

# Watch brewing progress until complete
machine-watch-progress:
    @watch -n 1 "curl -s {{machine-url}}/progress | jq"