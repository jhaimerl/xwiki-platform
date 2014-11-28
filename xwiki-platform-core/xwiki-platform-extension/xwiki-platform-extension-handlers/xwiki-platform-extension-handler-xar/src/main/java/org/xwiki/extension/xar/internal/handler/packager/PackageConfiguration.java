/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.extension.xar.internal.handler.packager;

import java.util.Set;

import org.xwiki.extension.xar.internal.handler.XarExtensionPlan;
import org.xwiki.job.event.status.JobStatus;
import org.xwiki.model.reference.DocumentReference;

/**
 * @version $Id$
 * @since 4.0M2
 */
public class PackageConfiguration
{
    private String wiki;

    private DocumentReference user;

    private boolean interactive;

    private JobStatus jobStatus;

    private boolean verbose = false;

    private XarExtensionPlan xarExtensionPlan;

    private Set<String> entriesToImport;

    private boolean skipMandatorytDocuments = true;

    public String getWiki()
    {
        return this.wiki;
    }

    public void setWiki(String wiki)
    {
        this.wiki = wiki;
    }

    public DocumentReference getUserReference()
    {
        return this.user;
    }

    public void setUser(DocumentReference user)
    {
        this.user = user;
    }

    public boolean isInteractive()
    {
        return this.interactive;
    }

    public void setInteractive(boolean interactive)
    {
        this.interactive = interactive;
    }

    public JobStatus getJobStatus()
    {
        return this.jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus)
    {
        this.jobStatus = jobStatus;
    }

    public boolean isVerbose()
    {
        return this.verbose;
    }

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public Set<String> getEntriesToImport()
    {
        return this.entriesToImport;
    }

    public void setEntriesToImport(Set<String> entriesToImport)
    {
        this.entriesToImport = entriesToImport;
    }

    public XarExtensionPlan getXarExtensionPlan()
    {
        return this.xarExtensionPlan;
    }

    public void setXarExtensionPlan(XarExtensionPlan xarExtensionPlan)
    {
        this.xarExtensionPlan = xarExtensionPlan;
    }

    public boolean isSkipMandatorytDocuments()
    {
        return this.skipMandatorytDocuments;
    }

    public void setSkipMandatorytDocuments(boolean skipMandatorytDocuments)
    {
        this.skipMandatorytDocuments = skipMandatorytDocuments;
    }
}
