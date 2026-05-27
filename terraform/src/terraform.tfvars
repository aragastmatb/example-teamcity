# ======================================================
# ПРИМЕР ФАЙЛА ПЕРЕМЕННЫХ
# Скопируйте в terraform.tfvars и заполните свои значения
# ======================================================

# --- Yandex Cloud ---
cloud_id                 = "b1g1a1e92iqgkqqscn3t"
folder_id                = "b1gf64c0gsqj5v61f3iv"
default_zone             = "ru-central1-a"
service_account_key_file = "~/.authorized_key.json"

# --- Окружение ---
environment = "dev"

# --- VPC ---
vpc_name       = "teamcity-net"
subnet_name    = "teamcity-sub"
v4_cidr_blocks = ["10.10.10.0/24"]

# --- Виртуальная машина ---
# vm_cores   = 2
# vm_memory  = 1
# vm_disk_size = 10
# preemptible     = true                     # false для prod, true для dev/staging


# --- SSH --- 
# с указанием файла         ssh_public_key = file("${pathexpand("~/.ssh/id_rsa.pub")}")
ssh_public_key    = "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAICDDF7T/y+EfIlq56HKlXxdU1oQAvU1Q7Hi1m+q/3pgM go@lab-srv"
allowed_ssh_cidr  = "0.0.0.0/0"

nexus_admin_password = "nexus_pswd_strong!"
