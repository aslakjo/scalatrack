package test.scala

import _root_.no.bekk.scala._
import _root_.no.bekk.scala.messages._
import org.scalatest.{FlatSpec, BeforeAndAfterEach}
import org.scalatest.matchers.ShouldMatchers
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.actor._


class ChallengeCollectionTests extends FlatSpec with ShouldMatchers with BeforeAndAfterEach{

  object AllChallenges extends Challenges{
    def list = challenges
  }

  "The collection of tests " should "all have correct answers" in{
                        val team = new Team("test")
    val server = actorOf(new Server with Challenges with PrintlineScoreBoardProvider).start


    val answers = AllChallenges.list.map(c => new Answer(team, new Question(c.question, c.content) ,c.answer))

    answers.foreach(a => (server !! a) should be (Some(Correct())))


  }
}