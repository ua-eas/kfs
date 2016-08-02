<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   -
   - Copyright 2005-2014 The Kuali Foundation
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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<jsp:useBean id="date" class="java.util.Date" />
<fmt:formatDate value="${date}" pattern="yyyy" var="currentYear" />
<kul:page docTitle="Kuali Acknowledgements" showDocumentInfo="false"
          headerTitle="Kuali Acknowledgements" transactionalDocument="false"
          renderInnerDiv="true" openNav="true">
    <h1>Acknowledgments</h1>

    <p>
        <img src="https://opensource.org/trademarks/osi-certified/web/osi-certified-60x50.png" align="right" border="0" /><br/>
        Copyright &copy; ${currentYear} Kuali, Inc. All rights reserved. This software is licensed for use pursuant to the Affero General
        Public License, version 3. Portions of this software are copyrighted by other parties, including the parties listed below,
        and you should see the licenses directory for complete copyright and licensing information.  Questions about licensing should
        be directed to
        <span class="nobr">
            <a href="mailto:${ConfigProperties.licensing.contact.email}" title="Send mail to ${ConfigProperties.licensing.contact.email}" rel="nofollow">${ConfigProperties.licensing.contact.email}</a>.
        </span>
    </p>

    <h2>Third Party Contributions</h2>

    <p>Portions of Kuali were developed by Indiana University, Cornell University, University of Hawaii, Michigan State University, University of Arizona, San Joaquin Delta Community College, University of California, University of Southern California, Colorado State University, University of Maryland and <a href="http://www.rsmart.com/">The rSmart Group.</a> (<span class="nobr"><a href="http://www.kuali.org/" title="kuali" rel="nofollow">http://www.kuali.org/</a></span>).</p>

    <p>This product includes software developed by the Apache Software Foundation (<span class="nobr"><a href="http://www.apache.org" title="apache" rel="nofollow">http://www.apache.org</a></span>).</p>

    <p>This product includes software developed by the JAX-RPC Project part of Project GlassFish (<span class="nobr"><a href="https://jax-rpc.dev.java.net/" title="jax-rpc" rel="nofollow">https://jax-rpc.dev.java.net/</a></span>).</p>

    <p>This product includes software developed by the SAAJ Project part of Project GlassFish (<span class="nobr"><a href="https://saaj.dev.java.net/" title="saaj" rel="nofollow">https://saaj.dev.java.net/</a></span>).</p>

    <p>This product includes software developed by Displaytag (<span class="nobr"><a href="http://displaytag.sourceforge.net/11/" title="displaytag" rel="nofollow">http://displaytag.sourceforge.net/11/</a></span>).</p>

    <p>This product includes software developed by the JDOM Project (<span class="nobr"><a href="http://www.jdom.org/" title="jdom" rel="nofollow">http://www.jdom.org/</a></span>).</p>

    <p>This product includes software developed by the University Corporation for Advanced Internet Development  Internet2 Project (<span class="nobr"><a href="http://www.ucaid.edu/" title="ucaid" rel="nofollow">http://www.ucaid.edu</a></span>).</p>

    <p>This product includes software developed by the Open Symphony Group (<span class="nobr"><a href="http://www.opensymphony.com/" title="opensymphony" rel="nofollow">http://www.opensymphony.com/</a></span>).</p>

    <p>This product includes software developed by the Indiana University Extreme&#33; Lab (<span class="nobr"><a href="http://www.extreme.indiana.edu/" title="extreme.indiana" rel="nofollow">http://www.extreme.indiana.edu/</a></span>).</p>

    <p>This product includes software developed by the SAXPath Project (<span class="nobr"><a href="http://www.saxpath.org/" title="saxpath" rel="nofollow">http://www.saxpath.org/</a></span>).</p>

    <p>This product includes software developed by the Working-Dogs.com (<span class="nobr"><a href="http://www.Working-Dogs.com/" title="Working-Dogs" rel="nofollow">http://www.Working-Dogs.com/</a></span>).</p>

    <p>This product includes software licensed under GNU Lesser General Public License (<span class="nobr"><a href="http://www.opensource.org/licenses/lgpl-license.php" title="LGPL" rel="nofollow">http://www.opensource.org/licenses/lgpl-license.php</a></span>).</p>

    <p>This product includes software licensed under Common Public License (<span class="nobr"><a href="http://www.opensource.org/licenses/cpl1.0.php" title="CPL" rel="nofollow">http://www.opensource.org/licenses/cpl1.0.php</a></span>).</p>

    <p>This product includes software licensed under the Mozilla Public License (<span class="nobr"><a href="http://www.mozilla.org/MPL/" title="MPL" rel="nofollow">http://www.mozilla.org/MPL/</a></span>).</p>

    <p>This product includes the Kuali Rice module licensed under the Kuali Foundation ECL (<span class="nobr"><a href="https://wiki.kuali.org/x/OwC1Eg" title="Rice Acknowledgments" rel="nofollow">Licensing and Acknowledgments (2.0)</a></span>).</p>

    <p>The original concept and code base for P6Spy was conceived and developed by Andy Martin, Ph.D. who generously contributed the first complete release to the public under this license.&nbsp; This product was due to the pioneering work of Andy that began in December of 1995 developing applications that could seamlessly be deployed with minimal effort but with dramatic results.&nbsp; This code is maintained and extended by Jeff Goke and with the ideas and contributions of other P6Spy contributors. (<span class="nobr"><a href="http://www.p6spy.com" title="p6spy" rel="nofollow">http://www.p6spy.com</a></span>)</p>

    <p>Portions Copyright &copy; 2005-${currentYear} Kuali Foundation. All Rights Reserved.</p>

    <p>Portions Copyright &copy; 2000-2005 INRIA, France Telecom. All Rights Reserved.</p>

    <p>Portions Copyright &copy; 2001-2006 Sun Microsystems, Inc. All Rights Reserved.</p>

    <p>Portions Copyright &copy; 1995-2006 International Business Machines Corporation and others. All Rights Reserved.</p>

    <p>Portions Copyright &copy; 2002, 2003 Mihai Bazon. All Rights Reserved.  Licensed under GNU Lesser General Public License (<span class="nobr"><a href="http://www.opensource.org/licenses/lgpl-license.php" title="LGPL" rel="nofollow">http://www.opensource.org/licenses/lgpl-license.php</a></span>).</p>

    <p>Portions Copyright &copy; 2005 Envoi Solutions LLC. All Rights Reserved. (<span class="nobr"><a href="http://xfire.codehaus.org" title="xfire" rel="nofollow">http://xfire.codehaus.org/</a></span>)</p>

    <p>Portions Copyright &copy; 2003-2006 The Werken Company. All Rights Reserved. (<span class="nobr"><a href="http://jaxen.codehaus.org/" title="jaxen" rel="nofollow">http://jaxen.codehaus.org/</a></span>)</p>

    <p>Portions Copyright &copy; 1999, 2004 Bull S. A. All Rights Reserved.<p>

    <p>Portions Copyright &copy; 2003-2005 Joe Walnes. All Rights Reserved. (<span class="nobr"><a href="http://xstream.codehaus.org/" title="xstream" rel="nofollow">http://xstream.codehaus.org/</a></span>)</p>

    <p>Portions Copyright &copy; 2002-2005, Jonas Bone&#769;r, Alexandre Vasseur. All rights reserved.<p>

    <p>Portions Copyright &copy; 1998-2003 World Wide Web Consortium (Massachusetts Institute of Technology, European Research Consortium for Informatics and Mathematics, Keio University). All Rights Reserved. This work is distributed under the W3C&reg; Software License in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.</p>
</kul:page>
