#!/bin/bash
# Script de despliegue para MicroShift en Red Hat
# Uso (dentro de la VM): bash deploy.sh
set -e

REGISTRY="localhost:5000"
NAMESPACE="crudlab"

echo "============================================"
echo "  CRUD Lab — Despliegue en MicroShift"
echo "============================================"

# 1. Registro local de contenedores
# IMPORTANTE: debe correr como root (sudo) con --network host para que
# CRI-O (motor de MicroShift) pueda alcanzar localhost:5000.
# Un registro rootless de Podman no es visible para procesos del sistema.
echo ""
echo "[1/7] Iniciando registro local de contenedores..."
# Detener el registro rootless (usuario) si existe — no es visible para CRI-O
podman stop local-registry 2>/dev/null || true
podman rm   local-registry 2>/dev/null || true
# Detener el registro root previo si existe
sudo podman rm -f local-registry 2>/dev/null || true

# Arrancar registro como root con --network host (accesible para CRI-O y para podman)
sudo podman run -d --name local-registry \
  --network host \
  docker.io/library/registry:2
echo "  Registro iniciado en host:5000."

# 2. Permitir registro inseguro en CRI-O (motor de contenedores de MicroShift)
echo ""
echo "[2/7] Configurando registro inseguro en CRI-O..."
sudo bash -c 'cat > /etc/containers/registries.conf.d/local-registry.conf <<EOF
[[registry]]
location = "localhost:5000"
insecure = true
EOF'
sudo systemctl restart crio
echo "  CRI-O reiniciado con registro local configurado."

# 3. Construir imagen de la aplicacion Spring Boot
echo ""
echo "[3/7] Construyendo imagen Spring Boot (puede tardar varios minutos)..."
podman build -t $REGISTRY/crudlab-app:latest .
echo "  Imagen crudlab-app lista."

# 4. Construir imagen de Nginx con el frontend integrado
echo ""
echo "[4/7] Construyendo imagen Nginx con frontend integrado..."
podman build -f nginx/Dockerfile -t $REGISTRY/crudlab-nginx:latest .
echo "  Imagen crudlab-nginx lista."

# 5. Descargar y re-etiquetar imagen de PostgreSQL
echo ""
echo "[5/7] Preparando imagen PostgreSQL..."
podman pull docker.io/postgres:16-alpine
podman tag docker.io/postgres:16-alpine $REGISTRY/postgres:16-alpine
echo "  Imagen postgres lista."

# 6. Publicar todas las imagenes en el registro local
# El registro corre en host:5000 (--network host), accesible tanto desde
# podman del usuario como desde CRI-O del sistema.
echo ""
echo "[6/7] Publicando imagenes en el registro local..."
podman push $REGISTRY/crudlab-app:latest   --tls-verify=false
podman push $REGISTRY/crudlab-nginx:latest --tls-verify=false
podman push $REGISTRY/postgres:16-alpine   --tls-verify=false
echo "  Imagenes publicadas."

# 7. Aplicar manifiestos en MicroShift
echo ""
echo "[7/7] Desplegando en MicroShift..."
oc apply -f k8s/

# Forzar recreacion de pods para que tomen las imagenes nuevas y la config nueva
# (necesario si los pods son de un intento anterior)
echo ""
echo "Reiniciando deployments para aplicar cambios..."
oc -n $NAMESPACE rollout restart deployment/postgres
oc -n $NAMESPACE rollout restart deployment/crudlab-app
oc -n $NAMESPACE rollout restart deployment/crudlab-nginx

echo ""
echo "Esperando que los deployments esten listos..."
oc -n $NAMESPACE rollout status deployment/postgres       --timeout=120s
oc -n $NAMESPACE rollout status deployment/crudlab-app   --timeout=300s
oc -n $NAMESPACE rollout status deployment/crudlab-nginx --timeout=60s

echo ""
echo "============================================"
echo "  Despliegue completado"
echo "============================================"
echo ""
oc get pods -n $NAMESPACE
echo ""
echo "URL de acceso:"
oc get route -n $NAMESPACE
