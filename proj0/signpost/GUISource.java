package signpost;

/**
 * A type of InputSource that receives commands from a GUI.
 *
 * @author P. N. Hilfinger
 */
class GUISource implements CommandSource {

    /**
     * Input source.
     */
    private GUI _source;

    /**
     * Provides input from SOURCE.
     */
    GUISource(GUI source) {
        _source = source;
    }

    @Override
    public String getCommand() {
        return _source.readCommand();
    }

}
