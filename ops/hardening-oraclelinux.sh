#!/usr/bin/env bash
set -euo pipefail

ADMIN_USER="${ADMIN_USER:-operador}"

dnf -y update
dnf -y install firewalld fail2ban policycoreutils-python-utils audit
systemctl enable --now firewalld
systemctl enable --now fail2ban
systemctl enable --now auditd

id "$ADMIN_USER" >/dev/null 2>&1 || useradd -m -G wheel "$ADMIN_USER"
echo "%wheel ALL=(ALL) ALL" >/etc/sudoers.d/wheel-admins
chmod 440 /etc/sudoers.d/wheel-admins

firewall-cmd --add-service=ssh --permanent
firewall-cmd --add-service=http --permanent
firewall-cmd --add-service=https --permanent
firewall-cmd --add-port=10000/tcp --permanent
firewall-cmd --add-port=30090/tcp --permanent
firewall-cmd --add-port=30300/tcp --permanent
firewall-cmd --reload

sed -i 's/^#\?PasswordAuthentication .*/PasswordAuthentication no/' /etc/ssh/sshd_config
sed -i 's/^#\?PermitRootLogin .*/PermitRootLogin prohibit-password/' /etc/ssh/sshd_config
systemctl restart sshd

echo "Hardening aplicado. Verifica acceso SSH por llave antes de cerrar la sesion actual."
