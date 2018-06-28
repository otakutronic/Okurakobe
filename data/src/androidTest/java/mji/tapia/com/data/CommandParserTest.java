package mji.tapia.com.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import mji.tapia.com.data.voice_command.VoiceCommand;
import mji.tapia.com.data.voice_command.VoiceCommandParser;
import mji.tapia.com.data.voice_command.VoiceCommandRepositoryImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Sami on 2/9/2018.
 */

@RunWith(AndroidJUnit4.class)
public class CommandParserTest {
    @Test
    public void findMatch() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        VoiceCommandParser voiceCommandParser = new VoiceCommandParser(appContext.getResources());
        VoiceCommandRepositoryImpl voiceCommandRepository = new VoiceCommandRepositoryImpl(voiceCommandParser);
        VoiceCommand  voiceCommand = voiceCommandRepository.getMatch(R.xml.command_activity_speech, "snjdfdkkdsfjkdsf");
        assertNull(voiceCommand);

        VoiceCommand  voiceCommand2 = voiceCommandRepository.getMatch(R.xml.command_activity_speech, "すべての電気をつけて");

        assertEquals("turn_on_all",voiceCommand2.getId());
    }
}
