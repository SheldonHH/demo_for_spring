```
\connect demodb
\dt
CREATE TABLE SUSERDATA (
	data_id serial PRIMARY KEY,
	username VARCHAR ( 50 ) UNIQUE NOT NULL,
    u1 INTEGER NOT NULL,
    u2 INTEGER NOT NULL,
	created_on TIMESTAMP NOT NULL,
        last_login TIMESTAMP 
);

DROP TABLE PERSON;
CREATE TABLE PERSON (
	person_id UUID PRIMARY KEY,
	name VARCHAR ( 50 ) UNIQUE NOT NULL
);

grant connect ON DATABSE PEER1_DB to peer1;
grant connect ON DATABSE PEER2_DB to peer2;

grant connect ON DATABSE SERVER1_DB to server1;
grant connect ON DATABSE SERVER2_DB to server2;

CREATE SCHEMA "schema_name"; 

grant select,insert,update,delete ON  ALL TABLES IN SCHEMA abc to abc;

```

```
创建用户
create user abc with ENCRYPTED password '';
GRANT abc to abc;
ALTER ROLE abc WITH abc;

##创建读写账号
CREATE ROLE abc_rw;
CREATE ROLE abc_rr;

##赋予访问数据库权限，schema权限
grant connect ON DATABASE abc to abc_rw;
GRANT USAGE ON SCHEMA scm TO abc_rw;
```
### List all tables
1. SQL Query
```
SELECT * FROM schema.tables;
SELECT * FROM schema.tables WHERE table_schema = 'singapore';
```



