<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2017 Kuali, Inc.
   -
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   -
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   -
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<div class="exportlinks">Export options:
<%--
KULRICE-5078: The MultipleValueLookup export functionality was failing in certain situations.  To force the multipleValueLookup use the export filter which disallows response flushing, 6578706f7274=1 was added to
each export url.    This represents a fixed parameter called PARAMETER_EXPORTING in TableTagParameters that is used by the export filter to understand when output should not be flushed.
--%>
<a href="multipleValueLookup.do?methodToCall=export&d-16544-e=1&businessObjectClassName=${KualiForm.businessObjectClassName}&lookupResultsSequenceNumber=${KualiForm.lookupResultsSequenceNumber}&6578706f7274=1" target="_blank"><span class="export csv">CSV </span></a>|
<a href="multipleValueLookup.do?methodToCall=export&d-16544-e=2&businessObjectClassName=${KualiForm.businessObjectClassName}&lookupResultsSequenceNumber=${KualiForm.lookupResultsSequenceNumber}&6578706f7274=1" target="_blank"><span class="export excel">spreadsheet </span></a>|
<a href="multipleValueLookup.do?methodToCall=export&d-16544-e=3&businessObjectClassName=${KualiForm.businessObjectClassName}&lookupResultsSequenceNumber=${KualiForm.lookupResultsSequenceNumber}&6578706f7274=1" target="_blank"><span class="export xml">XML </span> </a>
</div>
