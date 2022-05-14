```
\c server1
DROP TABLE IF EXISTS U_PERSON_DATA; 
CREATE TABLE U_PERSON_DATA (
	data_id UUID PRIMARY KEY,
	name VARCHAR ( 50 )  NOT NULL,
	u1 text[],
	u2 text[],
	verified boolean	
);
ALTER TABLE U_PERSON_DATA OWNER TO server1;
```