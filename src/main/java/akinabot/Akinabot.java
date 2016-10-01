package akinabot;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Injector;
import com.pengrad.telegrambot.model.Update;

import akinabot.model.bot.QuestionAnswer;
import akinabot.verticle.MessageSenderVerticle;
import akinabot.verticle.QuestionAnswerVerticle;
import akinabot.verticle.TelegramUpdateVerticle;
import akinabot.verticle.codec.QuestionAnswerCodec;
import akinabot.verticle.codec.UpdateCodec;
import akinabot.verticle.di.InjectorVerticleFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;

public class Akinabot extends AbstractVerticle {
	public static final String AKINATOR_API_URL = "http://api-en4.akinator.com";

	public static final String BUS_BOT_GREETINGS = "bot.greetings";
	public static final String BUS_BOT_IQUESTION = "bot.iquestion";
	public static final String BUS_BOT_QUESTION = "bot.question";
	public static final String BUS_BOT_RESULT = "bot.result";
	public static final String BUS_BOT_SORRY = "bot.sorry";
	public static final String BUS_BOT_UPDATE = "bot.update";

	public static final String BUTTON_PLAYAGAIN = "Play Again";
	public static final String BUTTON_PLAYNOW = "Play Now";
	public static final String BUTTON_QUIT = "Quit";
	public static final String BUTTON_START = "/start";

	@Override
	public void start() throws Exception {
		Json.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		InjectorVerticleFactory injectorVerticleFactory = new InjectorVerticleFactory();
		
		vertx.registerVerticleFactory(injectorVerticleFactory);
		Injector injector = injectorVerticleFactory.getInjector();
		
		vertx.eventBus()
				.registerDefaultCodec(QuestionAnswer.class, injector.getInstance(QuestionAnswerCodec.class))
				.registerDefaultCodec(Update.class, injector.getInstance(UpdateCodec.class));

		vertx.deployVerticle("java-inject:" + TelegramUpdateVerticle.class.getName());
		vertx.deployVerticle(QuestionAnswerVerticle.class.getName());
		vertx.deployVerticle("java-inject:" + MessageSenderVerticle.class.getName());
	}
}
