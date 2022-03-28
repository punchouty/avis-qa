DROP TABLE IF EXISTS location;
CREATE TABLE location (
  id INT GENERATED ALWAYS AS IDENTITY,
  company_code VARCHAR(255),
  name VARCHAR(255) NOT NULL,
  city VARCHAR(255) NOT NULL,
  PRIMARY KEY(id)
);
insert into location (company_code, name, city) values ('ABG01', 'name-1', 'new york');
insert into location (company_code, name, city) values ('ABG02', 'name-2', 'boston');
insert into location (company_code, name, city) values ('ABG03', 'name-3', 'washington dc');
insert into location (company_code, name, city) values ('ABG04', 'name-4', 'dallas');