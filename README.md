export MINIO_ENDPOINT=http://localhost:9000
export MINIO_ACCESS_KEY=ECjXT8yJQM3wTRNTEzYR
export MINIO_SECRET_KEY=JaFTj7HCsvlt7Vvvkl6pvDriwX87q6SrzcqcP5qu
export MINIO_BUCKET=medcase-dev


nohup java -jar medcase-api.jar --spring.profiles.active=suyh  &


小张哥服务器IP: 121.43.98.35
上传地址：/home/medcase/medcase-api
scp medcase-api/target/medcase-api.jar medcase@121.43.98.35:/home/medcase/medcase-api/

