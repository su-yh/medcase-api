export MINIO_ENDPOINT=http://localhost:9000
export MINIO_ACCESS_KEY=ECjXT8yJQM3wTRNTEzYR
export MINIO_SECRET_KEY=JaFTj7HCsvlt7Vvvkl6pvDriwX87q6SrzcqcP5qu
export MINIO_BUCKET=medcase-dev


nohup java -jar medcase-api.jar --spring.profiles.active=suyh  &