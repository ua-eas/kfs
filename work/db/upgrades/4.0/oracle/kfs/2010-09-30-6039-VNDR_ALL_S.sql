

alter sequence VNDR_ADDR_GNRTD_ID INCREMENT BY 8000;
select VNDR_ADDR_GNRTD_ID.nextval LAST_RUN from dual;
alter sequence VNDR_ADDR_GNRTD_ID INCREMENT BY 1;

alter sequence VNDR_CNTCT_GNRTD_ID INCREMENT BY 8000;
select VNDR_CNTCT_GNRTD_ID.nextval LAST_RUN from dual;
alter sequence VNDR_CNTCT_GNRTD_ID INCREMENT BY 1;

alter sequence VNDR_CNTCT_PHN_GNRTD_ID INCREMENT BY 8000;
select VNDR_CNTCT_PHN_GNRTD_ID.nextval LAST_RUN from dual;
alter sequence VNDR_CNTCT_PHN_GNRTD_ID INCREMENT BY 1;

alter sequence VNDR_CONTR_GNRTD_ID INCREMENT BY 8000;
select VNDR_CONTR_GNRTD_ID.nextval LAST_RUN from dual;
alter sequence VNDR_CONTR_GNRTD_ID INCREMENT BY 1;

alter sequence VNDR_CUST_NBR_GNRTD_ID INCREMENT BY 8000;
select VNDR_CUST_NBR_GNRTD_ID.nextval LAST_RUN from dual;
alter sequence VNDR_CUST_NBR_GNRTD_ID INCREMENT BY 1;

alter sequence VNDR_DFLT_ADDR_GNRTD_ID INCREMENT BY 8000;
select VNDR_DFLT_ADDR_GNRTD_ID.nextval LAST_RUN from dual;
alter sequence VNDR_DFLT_ADDR_GNRTD_ID INCREMENT BY 1;

alter sequence VNDR_HDR_GNRTD_ID INCREMENT BY 8000;
select VNDR_HDR_GNRTD_ID.nextval LAST_RUN from dual;
alter sequence VNDR_HDR_GNRTD_ID INCREMENT BY 1;

alter sequence VNDR_PHN_GNRTD_ID INCREMENT BY 8000;
select VNDR_PHN_GNRTD_ID.nextval LAST_RUN from dual;
alter sequence VNDR_PHN_GNRTD_ID INCREMENT BY 1;

alter sequence VNDR_STPLTN_ID INCREMENT BY 8000;
select VNDR_STPLTN_ID.nextval LAST_RUN from dual;
alter sequence VNDR_STPLTN_ID INCREMENT BY 1;

alter sequence VNDR_TAX_CHG_GNRTD_ID INCREMENT BY 8000;
select VNDR_TAX_CHG_GNRTD_ID.nextval LAST_RUN from dual;
alter sequence VNDR_TAX_CHG_GNRTD_ID INCREMENT BY 1;

