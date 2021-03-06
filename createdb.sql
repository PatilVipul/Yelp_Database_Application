CREATE TABLE YELP_USER(SINCE DATE NOT NULL, FUNNY_VOTES NUMBER NOT NULL, USEFUL_VOTES NUMBER NOT NULL, COOL_VOTES NUMBER NOT NULL, REVIEW_COUNT NUMBER NOT NULL, USERNAME VARCHAR(100) NOT NULL, USER_ID VARCHAR(100) PRIMARY KEY, FANS NUMBER NOT NULL, AVG_STARS NUMBER NOT NULL, TYPE VARCHAR(100) NOT NULL);
CREATE TABLE FRIENDS_TABLE(USER_ID VARCHAR(100) NOT NULL REFERENCES YELP_USER(USER_ID), FRIEND_ID VARCHAR(100) NOT NULL);
CREATE TABLE COMPLIMENTS_TABLE(USER_ID VARCHAR(100) NOT NULL REFERENCES YELP_USER(USER_ID), COMPLIMENT_TYPE VARCHAR(100) NOT NULL, COMPLIMENT_COUNT NUMBER NOT NULL);
CREATE TABLE ELITE_TABLE(USER_ID VARCHAR(100) NOT NULL REFERENCES YELP_USER(USER_ID), ELITE_YEARS NUMBER NOT NULL);

CREATE TABLE BUSINESS(BUSINESS_ID VARCHAR(100) PRIMARY KEY, FULL_ADDRESS VARCHAR(400) NOT NULL, OPEN_CLOSED VARCHAR(100) NOT NULL, CITY_NAME VARCHAR(100) NOT NULL, STATE_NAME VARCHAR(100) NOT NULL, LATITUDE NUMBER NOT NULL, LONGITUDE NUMBER NOT NULL, REVIEW_COUNT NUMBER NOT NULL, BUSINESS_NAME VARCHAR(200) NOT NULL, STARS NUMBER NOT NULL, TYPE VARCHAR(100) NOT NULL);
CREATE TABLE BUSINESS_HOURS(BUSINESS_ID VARCHAR(100) NOT NULL REFERENCES BUSINESS(BUSINESS_ID), BUSINESS_DAY VARCHAR(100) NOT NULL, OPEN_TIME NUMBER NOT NULL, CLOSE_TIME NUMBER NOT NULL);
CREATE TABLE NEIGHBORHOOD(BUSINESS_ID VARCHAR(100) NOT NULL REFERENCES BUSINESS(BUSINESS_ID), NEIGHBORS VARCHAR(100) NOT NULL);
CREATE TABLE BUSINESS_CATEGORY_TABLE(BUSINESS_ID VARCHAR(100) NOT NULL REFERENCES BUSINESS(BUSINESS_ID), MAIN_CATEGORY VARCHAR(100) NOT NULL, SUB_CATEGORY VARCHAR(100));
CREATE TABLE BUSINESS_ATTRIBUTES(BUSINESS_ID VARCHAR(100) NOT NULL REFERENCES BUSINESS(BUSINESS_ID), ATTRIBUTE_NAME VARCHAR(100) NOT NULL);

CREATE TABLE REVIEWS_TABLE(FUNNY_VOTES NUMBER NOT NULL, USEFUL_VOTES NUMBER NOT NULL, COOL_VOTES NUMBER NOT NULL, USER_ID VARCHAR(100) NOT NULL REFERENCES YELP_USER(USER_ID), REVIEW_ID VARCHAR(100) PRIMARY KEY, STARS NUMBER NOT NULL, REVIEW_DATE DATE NOT NULL, REVIEW_TEXT VARCHAR(3000) NOT NULL, TYPE VARCHAR(100) NOT NULL, BUSINESS_ID VARCHAR(100) NOT NULL REFERENCES BUSINESS(BUSINESS_ID));

CREATE TABLE CHECKIN_TABLE(CHECKIN_INFO VARCHAR(2000) NOT NULL, TYPE VARCHAR(50), BUSINESS_ID VARCHAR(100) NOT NULL REFERENCES BUSINESS(BUSINESS_ID));