#!/usr/bin/env bash
set -euo pipefail

echo "Instalacion base de VirtualMin en Oracle Linux 10.1"
echo "Ejecutar solo en el VPS que expondras para administracion."

dnf -y update
dnf -y install wget perl firewalld
systemctl enable --now firewalld

wget -O /root/virtualmin-install.sh https://software.virtualmin.com/gpl/scripts/virtualmin-install.sh
chmod +x /root/virtualmin-install.sh
/root/virtualmin-install.sh --yes

firewall-cmd --add-port=10000/tcp --permanent
firewall-cmd --reload

echo "VirtualMin quedara disponible en: https://IP_PUBLICA:10000"
echo "Usuario: root o usuario sudo configurado en el VPS"
