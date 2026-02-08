#!/bin/bash

##############################################################################
# JWT Key Generator - PKCS8 Format
# Generates RSA private key in PKCS8 format with public key and certificate
##############################################################################

# Configuration
KEY_SIZE=2048
DAYS_VALID=365
OUTPUT_DIR="."

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔══════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║         JWT Key Generator - PKCS8 Format                 ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════════════════╝${NC}"
echo ""

# Create output directory
mkdir -p $OUTPUT_DIR

echo -e "${YELLOW} Generating RSA Private Key in PKCS8 format...${NC}"
echo "   Key size: $KEY_SIZE bits"
echo "   Output directory: $OUTPUT_DIR"
echo ""

# 1. Generate private key in PKCS8 format
openssl genpkey -algorithm RSA \
  -out $OUTPUT_DIR/private-key-pkcs8.pem \
  -pkeyopt rsa_keygen_bits:$KEY_SIZE

if [ $? -eq 0 ]; then
    echo -e "${GREEN} Private key generated: $OUTPUT_DIR/private-key-pkcs8.pem${NC}"
else
    echo -e "${RED} Failed to generate private key${NC}"
    exit 1
fi

# 2. Generate public key
echo -e "${YELLOW} Generating public key...${NC}"
openssl rsa -pubout \
  -in $OUTPUT_DIR/private-key-pkcs8.pem \
  -out $OUTPUT_DIR/public-key.pem

if [ $? -eq 0 ]; then
    echo -e "${GREEN} Public key generated: $OUTPUT_DIR/public-key.pem${NC}"
else
    echo -e "${RED} Failed to generate public key${NC}"
    exit 1
fi

# 3. Generate self-signed certificate
echo -e "${YELLOW} Generating self-signed certificate...${NC}"
openssl req -new -x509 \
  -key $OUTPUT_DIR/private-key-pkcs8.pem \
  -out $OUTPUT_DIR/certificate.crt \
  -days $DAYS_VALID \
  -subj "/C=IN/ST=KA/L=Bangalore/O=YourCompany/OU=Payment/CN=payment-service"

if [ $? -eq 0 ]; then
    echo -e "${GREEN} Certificate generated: $OUTPUT_DIR/certificate.crt${NC}"
else
    echo -e "${RED} Failed to generate certificate${NC}"
    exit 1
fi

# 4. Set proper permissions
chmod 600 $OUTPUT_DIR/private-key-pkcs8.pem
chmod 644 $OUTPUT_DIR/public-key.pem
chmod 644 $OUTPUT_DIR/certificate.crt

echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                    Key Information                        ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
echo ""

# Display private key information
echo -e "${YELLOW}Private Key:${NC}"
openssl rsa -in $OUTPUT_DIR/private-key-pkcs8.pem -text -noout | head -1
echo ""

# Verify PKCS8 format
echo -e "${YELLOW}Format Verification:${NC}"
openssl pkey -in $OUTPUT_DIR/private-key-pkcs8.pem -text -noout | grep "PKCS" && \
  echo -e "${GREEN} Confirmed PKCS#8 format${NC}" || \
  echo -e "${RED} Not in PKCS#8 format${NC}"
echo ""

# Display certificate information
echo -e "${YELLOW}Certificate:${NC}"
openssl x509 -in $OUTPUT_DIR/certificate.crt -noout -subject -dates
echo ""

echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN} All keys generated successfully!${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════${NC}"
echo ""
echo "Files created:"
echo -e "  ${GREEN}✓${NC} $OUTPUT_DIR/private-key-pkcs8.pem  (PKCS8 Private Key - ${RED}Keep Secret!${NC})"
echo -e "  ${GREEN}✓${NC} $OUTPUT_DIR/public-key.pem         (Public Key)"
echo -e "  ${GREEN}✓${NC} $OUTPUT_DIR/certificate.crt        (X.509 Certificate)"
echo ""
echo ""
echo -e "${GREEN} You're ready to use JWT tokens!${NC}"
echo ""