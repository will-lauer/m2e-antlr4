package com.yahoo.wlauer.m2e.antlr4;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class Antlr4BuildParticipant extends MojoExecutionBuildParticipant {
	
	Antlr4BuildParticipant (MojoExecution execution)
	{
		super(execution, true);
	}
	
	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        IMaven maven = MavenPlugin.getMaven();
        BuildContext buildContext = getBuildContext();


        // check if any of the grammar files changed
        MavenProject project = getMavenProjectFacade().getMavenProject(monitor);
        File source = maven.getMojoParameterValue(project, getMojoExecution(), "sourceDirectory", File.class, monitor);
        Scanner ds = buildContext.newScanner( source ); // delta or full scanner
        ds.scan();
        String[] includedFiles = ds.getIncludedFiles();
        if (includedFiles == null || includedFiles.length <= 0 )
        {
            return null;
        }


        // execute mojo
        Set<IProject> result = super.build( kind, monitor );


        // tell m2e builder to refresh generated files
        File generated = maven.getMojoParameterValue(project, getMojoExecution(), "outputDirectory", File.class, monitor);
        if (generated != null) {
            buildContext.refresh( generated );
        }

        return result;
	}

}
