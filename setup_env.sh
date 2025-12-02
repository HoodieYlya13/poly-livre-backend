#!/bin/bash

ENV_FILE=".env"

# Default values
POSTGRES_USER="test"
POSTGRES_PASSWORD="password"
POSTGRES_DB="polylivretestdb"
JWT_PRIVATE_KEY="MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgO8Nm9Rv9Kz2J7TLtdoIjHDL5Mo4WCWS0ixQfGwxBqGahRANCAAQpbN6OWGmhJFO99NzDu89U2HjIFzIDw8417Y7LFqTJ3+FJXv99EDPjHfJhZKkuD66FEjUflmadCFHdy1dkss1j"
JWT_PUBLIC_KEY="MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEKWzejlhpoSRTvfTcw7vPVNh4yBcyA8PONe2Oyxakyd/hSV7/fRAz4x3yYWSpLg+uhRI1H5ZmnQhR3ctXZLLNYw=="

if [ ! -f "$ENV_FILE" ]; then
    echo "Creating $ENV_FILE with default values..."
    cat <<EOF > "$ENV_FILE"
POSTGRES_USER=$POSTGRES_USER
POSTGRES_PASSWORD=$POSTGRES_PASSWORD
POSTGRES_DB=$POSTGRES_DB
JWT_PRIVATE_KEY="$JWT_PRIVATE_KEY"
JWT_PUBLIC_KEY="$JWT_PUBLIC_KEY"
EOF
    echo "$ENV_FILE created successfully."
else
    echo "$ENV_FILE exists. Verifying variables..."
    MISSING=0
    
    if ! grep -q "^POSTGRES_USER=" "$ENV_FILE"; then MISSING=1; echo "Missing POSTGRES_USER"; fi
    if ! grep -q "^POSTGRES_PASSWORD=" "$ENV_FILE"; then MISSING=1; echo "Missing POSTGRES_PASSWORD"; fi
    if ! grep -q "^POSTGRES_DB=" "$ENV_FILE"; then MISSING=1; echo "Missing POSTGRES_DB"; fi
    if ! grep -q "^JWT_PRIVATE_KEY=" "$ENV_FILE"; then MISSING=1; echo "Missing JWT_PRIVATE_KEY"; fi
    if ! grep -q "^JWT_PUBLIC_KEY=" "$ENV_FILE"; then MISSING=1; echo "Missing JWT_PUBLIC_KEY"; fi

    if [ $MISSING -eq 1 ]; then
        echo "The .env file is missing some required variables."
        echo "Please ensure all required variables are present."
        exit 1
    else
        echo "All required variables are present."
    fi
fi
