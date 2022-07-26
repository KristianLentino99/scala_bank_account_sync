<h2>README</h2>

To make it work you should copy the `.env.sample` file  with `cp .env.sample .env`and put here your Nordigen keys:
````
export NORDIGEN_SECRET_ID= YOUR_SECRET_ID
export NORDIGEN_SECRET_KEY= YOUR_SECRET_KEY
````

After that run ```source .env```

Now you can run ````sbt run```` or even better:

```sbt``` and after you are prompted into the console you should run:
```~reStart``` this will give you the **hot-reload feature**