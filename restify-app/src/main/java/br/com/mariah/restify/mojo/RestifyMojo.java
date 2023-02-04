package br.com.mariah.restify.mojo;

import br.com.mariah.restify.RestifyApplication;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "restify", defaultPhase = LifecyclePhase.COMPILE)
public class RestifyMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {


        getLog().info("chamou a execução!");

        new RestifyApplication().generate();
    }
}
