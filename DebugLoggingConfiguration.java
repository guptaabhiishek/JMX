package com.configuration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Created on 5/4/16.
 */
@Configuration
@ManagedResource(objectName = "nameofobjectinJMX:name=DebugManager",
        description = "Control to allow REST entry / exit debug to be toggled")
public class DebugLoggingConfiguration {

    private static final Logger restLogger = (Logger) LoggerFactory.getLogger("com.aspects.RequestMappingAspect");
    //
    private Level level = Level.INFO;  // default to not showing debug info

    /**
     * Set the logging level for REST entry / exit to DEBUG
     */
    @ManagedOperation(description = "Enable DEBUG level tracking of REST calls")
    public void setLoggingToDebug() {
        level = Level.DEBUG;
        restLogger.setLevel(level);
    }

    /**
     * Set the logging level for REST entry / exit to INFO
     */
    @ManagedOperation(description = "Enable INFO level tracking of REST calls")
    public void setLoggingToInfo() {
        level = Level.INFO;
        restLogger.setLevel(level);
    }

    /**
     * Show the present logging level
     *
     * @return Present logging level
     */
    @ManagedAttribute(description = "Show logging level for REST calls")
    public String getDebugStatus() {
        return level.levelStr;
    }
}
