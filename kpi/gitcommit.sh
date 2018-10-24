cd /home/rjavila/ELLDEVE_JM/customer-software/
git pull origin elldeve
cd /home/rjavila/ELLDEVE_JM/customer-software/$1/src
cp /home/rjavila/$2.groovy /home/rjavila/ELLDEVE_JM/customer-software/$1/src/$2.groovy
git add $2.groovy
git commit -m "Nueva version $2.groovy"
git push origin elldeve