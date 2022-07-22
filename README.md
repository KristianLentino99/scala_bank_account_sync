<h2>README</h2>

To make it work yo should create a ´.env.sample´ file and put here your Nordigen keys:
````
export NORDIGEN_SECRET_ID= YOUR_SECRET_ID
export NORDIGEN_SECRET_KEY= YOUR_SECRET_KEY
````

then run ```source env.sample```

After that you can run ````sbt run```` or even better:
```sbt``` and after you are prompted into the console you should run:
```~reStart``` this will give you the **hot-reload feature**