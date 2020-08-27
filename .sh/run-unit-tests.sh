#! /bin/sh -
cat << "EOF"
  ___ ___ ___ ___ _    ___ _  _ ___          ___  _  _ ___
 | _ \_ _| _ \ __| |  |_ _| \| | __|  ___   / _ \| \| | __|
 |  _/| ||  _/ _|| |__ | || .` | _|  |___| | (_) | .` | _|
 |_| |___|_| |___|____|___|_|\_|___|        \___/|_|\_|___|

 ----------------------------------------------------------
          _   _      _ _     _____       _
         | | | |_ _ (_) |_  |_   _|__ __| |_ ___
         | |_| | ' \| |  _|   | |/ -_|_-<  _(_-<
          \___/|_||_|_|\__|   |_|\___/__/\__/__/

EOF

chmod +x .sh/datastore.emulator.command
open .sh/datastore.emulator.command

echo "Sleeping script 5 secs while the Google Datastore Emulator gets started"
sleep 5s

echo "Datastore environment init"
$(gcloud beta emulators datastore env-init)

mvn test

echo "Datastore environment unset"
$(gcloud beta emulators datastore env-unset)
