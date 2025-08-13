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

    if ! command -v gum &>/dev/null; then
        brew install gum
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

    if ! command -v gum >/dev/null 2>&1; then
        cd /tmp
        GUM_VERSION="0.14.3" # Known good version
        wget -qO gum.deb "https://github.com/charmbracelet/gum/releases/download/v${GUM_VERSION}/gum_${GUM_VERSION}_amd64.deb"
        sudo apt-get install -y --allow-downgrades ./gum.deb
        rm gum.deb
        cd - >/dev/null
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

source ~/.local/share/setup-my-computer/install