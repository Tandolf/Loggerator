package config;

public class LoggerConfiguration {

    private Object appender;

    private LoggerConfiguration(Object appender) {
        this.appender = appender;
    }

    public static LoggerConfigurationBuilder builder() {
        return new LoggerConfiguration.LoggerConfigurationBuilder();
    }


    public static class LoggerConfigurationBuilder {
        private Object appender;

        public LoggerConfiguration.LoggerConfigurationBuilder setAppender(Object appender) {
            this.appender = appender;
            return this;
        }

        public LoggerConfiguration build() {
            return new LoggerConfiguration(appender);
        }
    }
}
