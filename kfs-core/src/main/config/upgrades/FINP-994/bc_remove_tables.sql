--
-- The Kuali Financial System, a comprehensive financial management system for higher education.
--
-- Copyright 2005-2017 Kuali, Inc.
--
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
--
-- You should have received a copy of the GNU Affero General Public License
-- along with this program.  If not, see <http://www.gnu.org/licenses/>.
--

/* Oracle */
alter table LD_BCNSTR_MONTH_T drop constraint LD_BCNSTR_MONTH_TR2;
alter table LD_BCN_ACCT_ORG_HIER_T drop constraint LD_BCN_ACCT_ORG_HIER_TR2;
alter table LD_BCN_ACCT_ORG_HIER_T drop constraint LD_BCN_ACCT_ORG_HIER_TR3;
alter table LD_BCN_ACCT_RPTS_T drop constraint LD_BCN_ACCT_RPTS_TR1;
alter table LD_BCN_ACCT_RPTS_T drop constraint LD_BCN_ACCT_RPTS_TR3;
alter table LD_BCN_ACCT_RPTS_T drop constraint LD_BCN_ACCT_RPTS_TR5;
alter table LD_BCN_AF_REASON_T drop constraint LD_BCN_AF_REASON_TR6;
alter table LD_BCN_AF_REASON_T drop constraint LD_BCN_AF_REASON_TR7;
alter table LD_BCN_CSF_TRCKR_T drop constraint LD_BCN_CSF_TRCKR_TR2;
alter table LD_BCN_ORG_RPTS_T drop constraint LD_BCN_ORG_RPTS_TR2;
alter table LD_BCN_ORG_RPTS_T drop constraint LD_BCN_ORG_RPTS_TR4;
alter table LD_BCN_ORG_RPTS_T drop constraint LD_BCN_ORG_RPTS_TR5;
alter table LD_BCN_POS_T drop constraint LD_BCN_POS_TR1;
alter table LD_BCN_POS_T drop constraint LD_BCN_POS_TR2;
alter table LD_CSFTRCKR_OVRD_T drop constraint LD_CSFTRCKR_OVRD_TR1;
alter table LD_CSFTRCKR_OVRD_T drop constraint LD_CSFTRCKR_OVRD_TR2;
alter table LD_CSFTRCKR_OVRD_T drop constraint LD_CSFTRCKR_OVRD_TR4;
alter table LD_PNDBC_APPTFND_T drop constraint LD_PNDBC_APPTFND_TR1;
alter table LD_PNDBC_APPTFND_T drop constraint LD_PNDBC_APPTFND_TR11;
alter table LD_PNDBC_APPTFND_T drop constraint LD_PNDBC_APPTFND_TR3;
alter table LD_PNDBC_APPTFND_T drop constraint LD_PNDBC_APPTFND_TR6;
alter table LD_PND_BCNSTR_GL_T drop constraint LD_PND_BCNSTR_GL_TR1;
alter table LD_PND_BCNSTR_GL_T drop constraint LD_PND_BCNSTR_GL_TR3;
alter table LD_PND_BCNSTR_GL_T drop constraint LD_PND_BCNSTR_GL_TR4;
alter table LD_PND_BCNSTR_GL_T drop constraint LD_PND_BCNSTR_GL_TR5;
alter table LD_PND_BCNSTR_GL_T drop constraint LD_PND_BCNSTR_GL_TR8;
alter table LD_PND_BCNSTR_GL_T drop constraint LD_PND_BCNSTR_GL_TR9;

/* MySql */
alter table LD_BCNSTR_MONTH_T drop foreign key LD_BCNSTR_MONTH_TR2;
alter table LD_BCN_ACCT_ORG_HIER_T drop foreign key LD_BCN_ACCT_ORG_HIER_TR2;
alter table LD_BCN_ACCT_ORG_HIER_T drop foreign key LD_BCN_ACCT_ORG_HIER_TR3;
alter table LD_BCN_ACCT_RPTS_T drop foreign key LD_BCN_ACCT_RPTS_TR1;
alter table LD_BCN_ACCT_RPTS_T drop foreign key LD_BCN_ACCT_RPTS_TR3;
alter table LD_BCN_ACCT_RPTS_T drop foreign key LD_BCN_ACCT_RPTS_TR5;
alter table LD_BCN_AF_REASON_T drop foreign key LD_BCN_AF_REASON_TR6;
alter table LD_BCN_AF_REASON_T drop foreign key LD_BCN_AF_REASON_TR7;
alter table LD_BCN_CSF_TRCKR_T drop foreign key LD_BCN_CSF_TRCKR_TR2;
alter table LD_BCN_ORG_RPTS_T drop foreign key LD_BCN_ORG_RPTS_TR2;
alter table LD_BCN_ORG_RPTS_T drop foreign key LD_BCN_ORG_RPTS_TR4;
alter table LD_BCN_ORG_RPTS_T drop foreign key LD_BCN_ORG_RPTS_TR5;
alter table LD_BCN_POS_T drop foreign key LD_BCN_POS_TR1;
alter table LD_BCN_POS_T drop foreign key LD_BCN_POS_TR2;
alter table LD_CSFTRCKR_OVRD_T drop foreign key LD_CSFTRCKR_OVRD_TR1;
alter table LD_CSFTRCKR_OVRD_T drop foreign key LD_CSFTRCKR_OVRD_TR2;
alter table LD_CSFTRCKR_OVRD_T drop foreign key LD_CSFTRCKR_OVRD_TR4;
alter table LD_PNDBC_APPTFND_T drop foreign key LD_PNDBC_APPTFND_TR1;
alter table LD_PNDBC_APPTFND_T drop foreign key LD_PNDBC_APPTFND_TR11;
alter table LD_PNDBC_APPTFND_T drop foreign key LD_PNDBC_APPTFND_TR3;
alter table LD_PNDBC_APPTFND_T drop foreign key LD_PNDBC_APPTFND_TR6;
alter table LD_PND_BCNSTR_GL_T drop foreign key LD_PND_BCNSTR_GL_TR1;
alter table LD_PND_BCNSTR_GL_T drop foreign key LD_PND_BCNSTR_GL_TR3;
alter table LD_PND_BCNSTR_GL_T drop foreign key LD_PND_BCNSTR_GL_TR4;
alter table LD_PND_BCNSTR_GL_T drop foreign key LD_PND_BCNSTR_GL_TR5;
alter table LD_PND_BCNSTR_GL_T drop foreign key LD_PND_BCNSTR_GL_TR8;
alter table LD_PND_BCNSTR_GL_T drop foreign key LD_PND_BCNSTR_GL_TR9;

/* MySql or Oracle */
drop table LD_BCNSTR_HDR_T;
drop table LD_BCNSTR_MONTH_T;
drop table LD_BCN_2PLG_LIST_MT;
drop table LD_BCN_ACCTSEL_T;
drop table LD_BCN_ACCT_BAL_T;
drop table LD_BCN_ACCT_DUMP_T;
drop table LD_BCN_ACCT_ORG_HIER_T;
drop table LD_BCN_ACCT_RPTS_T;
drop table LD_BCN_ACCT_SUMM_T;
drop table LD_BCN_ACTV_JOB_MT;
drop table LD_BCN_ADM_POST_T;
drop table LD_BCN_AF_LOAD01_MT;
drop table LD_BCN_AF_LOAD02_MT;
drop table LD_BCN_AF_LOAD_T;
drop table LD_BCN_AF_REASON_T;
drop table LD_BCN_AF_RSN_CD_T;
drop table LD_BCN_BAL_BY_ACCT_T;
drop table LD_BCN_CSF_TRCKR_T;
drop table LD_BCN_CTRL_LIST_T;
drop table LD_BCN_DURATION_T;
drop table LD_BCN_FND_LOCK_T;
drop table LD_BCN_II_INIT_MT;
drop table LD_BCN_INCUMBENT_SEL_T;
drop table LD_BCN_INTINCBNT_T;
drop table LD_BCN_LEVL_SUMM_T;
drop table LD_BCN_MNTH_SUMM_T;
drop table LD_BCN_OBJT_DUMP_T;
drop table LD_BCN_OBJT_SUMM_T;
drop table LD_BCN_OBJ_PICK_T;
drop table LD_BCN_ORG_RPTS_T;
drop table LD_BCN_PAYRT_HLDG_T;
drop table LD_BCN_POS_FND_T;
drop table LD_BCN_POS_INIT_MT;
drop table LD_BCN_POS_SEL_T;
drop table LD_BCN_POS_T;
drop table LD_BCN_PULLUP_T;
drop table LD_BCN_RQST_MT;
drop table LD_BCN_RSN_CD_PK_T;
drop table LD_BCN_SAL_FND_T;
drop table LD_BCN_SAL_SSN_T;
drop table LD_BCN_SLRY_TOT_T;
drop table LD_BCN_SUBFUND_PICK_T;
drop table LD_CSFTRCKR_OVRD_T;
drop table LD_PNDBC_APPTFND_T;
drop table LD_PND_BCNSTR_GL_T;
