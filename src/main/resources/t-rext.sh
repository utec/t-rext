curl -L https://github.com/jrichardsz-software-architect-tools/t-rext/releases/latest/download/t-rext.jar > t-rext.jar


echo 'java -jar $TREXT_HOME/t-rext.jar "$@"' > t-rext

echo ""

echo "Export these variables or set them in your correct profile file (~/.bash_profile, ~/.zshrc, ~/.profile, or ~/.bashrc)."

echo ""

echo "export TREXT_HOME=$(pwd)"
echo 'export PATH=$PATH:$TREXT_HOME'

echo ""
echo "Give execution permissions to the script"
echo "sudo chmod +x $(pwd)/t-rext"