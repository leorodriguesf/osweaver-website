#!/usr/bin/env bash

set -e

OS_TYPE=$(uname -s)

cd ~

case "$OS_TYPE" in
Darwin) 
    OS=macos
    ;;
Linux)
    if [ ! -f /etc/os-release ]; then
        echo "Not able to detect Linux distro. Stopping"
        exit 1
    fi

    . /etc/os-release

    case "$ID" in
    ubuntu)
        OS=ubuntu
        ;;
    *)
        echo "Linux distro $NAME not supported. Stopping"
        exit 1
        ;;
    esac
    ;;
*)
    echo "OS $OS_TYPE not yet supported. Stopping"
    exit 1
    ;;
esac

echo -e "=> $OS detected\n"

echo "Installing $OS dependencies"

case "$OS" in
macos) 
    # Keep sudo alive
    sudo -v

    # Prevent the system from sleeping while the script is running
    caffeinate -s -u -w $$ &

    # Installing brew will also install dev tools (including git)
    if [ ! -f /opt/homebrew/bin/brew ]; then
        NONINTERACTIVE=1 /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    fi  
    ;;
ubuntu)
    if [ "$(id -u)" -eq 0 ]; then
        apt-get update && apt-get install -y sudo
    else
        sudo apt-get update
    fi

    if ! command -v git &>/dev/null; then
        sudo apt-get install git -y
    fi
    ;;
*)
    echo "OS $OS not yet supported. Stopping"
    exit 1
    ;;
esac

echo "$OS dependencies installed"

echo "Cloning repository..."

rm -rf ~/.local/share/setup-my-computer

git clone https://github.com/leorodriguesf/setup-my-computer.git ~/.local/share/setup-my-computer >/dev/null