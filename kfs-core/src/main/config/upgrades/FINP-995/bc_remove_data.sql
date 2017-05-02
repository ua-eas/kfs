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

/* Financials */
DELETE FROM krcr_parm_t WHERE nmspc_cd IN ('KFS-BC');
DELETE FROM krcr_cmpnt_t WHERE nmspc_cd IN ('KFS-BC');
DELETE FROM krcr_nmspc_t WHERE nmspc_cd IN ('KFS-BC');

/* Rice */
DELETE FROM krim_pnd_role_perm_t WHERE perm_id in ('296','266','228','247','225','229','295','251','297');
DELETE FROM krim_pnd_role_perm_t WHERE perm_id in ('296','266','228','247','225','229','295','251','297');
DELETE FROM krim_pnd_role_perm_t WHERE role_id in ('75','80','81');
DELETE FROM krim_pnd_role_rsp_t WHERE role_id in ('75','80','81');
DELETE FROM krim_perm_attr_data_t WHERE perm_id in ('296','266','228','247','225','229','295','251','297');
DELETE FROM krim_role_perm_t WHERE perm_id in ('296','266','228','247','225','229','295','251','297');
DELETE FROM krim_perm_t WHERE perm_id in ('296','266','228','247','225','229','295','251','297');
DELETE FROM krim_role_perm_t WHERE role_id in ('75','80','81');
DELETE FROM krim_role_rsp_t WHERE role_id in ('75','80','81');
DELETE FROM krim_role_document_t WHERE role_id in ('75','80','81');
DELETE FROM krim_role_mbr_attr_data_t WHERE role_mbr_id in ('KFS1602','KFS1603','KFS1604','KFS1605','KFS1618','KFS1644','KFS1614','KFS1623','KFS1615','KFS1654','KFS1634','KFS1631','KFS1632','KFS1627','KFS1619','KFS1646','KFS1630','KFS1637','KFS1613','KFS1612','KFS1621','KFS1642','KFS1616','KFS1651','KFS1648','KFS1629','KFS1338','KFS1641','KFS1617','KFS1624','KFS1622','KFS1643','KFS1638','KFS1647','KFS1625','KFS1645','KFS1636','KFS1649','KFS1633','KFS1628','KFS1620','KFS1652','KFS1635','KFS1626','KFS1650','KFS1653','KFS1340','KFS1341','KFS1342','KFS1343','KFS1344');
DELETE FROM krim_role_rsp_actn_t WHERE role_mbr_id in ('KFS1602','KFS1603','KFS1604','KFS1605','KFS1618','KFS1644','KFS1614','KFS1623','KFS1615','KFS1654','KFS1634','KFS1631','KFS1632','KFS1627','KFS1619','KFS1646','KFS1630','KFS1637','KFS1613','KFS1612','KFS1621','KFS1642','KFS1616','KFS1651','KFS1648','KFS1629','KFS1338','KFS1641','KFS1617','KFS1624','KFS1622','KFS1643','KFS1638','KFS1647','KFS1625','KFS1645','KFS1636','KFS1649','KFS1633','KFS1628','KFS1620','KFS1652','KFS1635','KFS1626','KFS1650','KFS1653','KFS1340','KFS1341','KFS1342','KFS1343','KFS1344');
DELETE FROM krim_role_mbr_t WHERE role_id in ('75','80','81');
DELETE FROM krim_role_t WHERE role_id in ('75','80','81');
DELETE FROM krcr_nmspc_t WHERE nmspc_cd IN ('KFS-BC');
