# Get coffee machine status
status:
    @curl -s {{backend-url}}/status | jq

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
        {{backend-url}}/order

# Get brewing progress
progress:
    @curl -s {{backend-url}}/progress | jq
