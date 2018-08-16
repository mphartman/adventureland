package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Adventure;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class AdventureScriptParsingRule implements TestRule {

    private AdventureScriptParser parser;

    private Reader reader;
    private Adventure adventure;

    public AdventureScriptParsingRule(AdventureScriptParser parser) {
        this.parser = parser;
    }

    public Adventure parse() {
        if (null == adventure) {
            try {
                try {
                    adventure = parser.parse(reader);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            finally {
                try {
                    reader.close();
                }
                catch (IOException ignore) {
                }
            }
        }
        return adventure;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                AdventureScriptResource resource = description.getAnnotation(AdventureScriptResource.class);
                if (null != resource) {
                    Class<?> clazz = resource.getClass();
                    InputStream is = clazz.getResourceAsStream(resource.value());
                    reader = new InputStreamReader(is);
                }
                base.evaluate();
            }
        };
    }
}
