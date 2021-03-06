
import _root_.no.bekk.scala._
import _root_.no.bekk.scala.messages._
import org.scalatest.{FlatSpec, BeforeAndAfterEach}
import org.scalatest.matchers.ShouldMatchers
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.actor._



trait TestChallenges
{  
  val challenges = List(
    new Challenge("challenge", "", "", (q:Question, tekst) => tekst.equals("answer")),
    new Challenge("challenge2","", "", (q:Question, tekst) => tekst.equals("answer"))
  )
}

class ServerTest extends FlatSpec with ShouldMatchers  with BeforeAndAfterEach {

  var server:ActorRef = null
  val team = new Team("test")

  var scoreBoardTeam:Team = null;
  var scoreBoardQuestion: Question = null
  var result: Option[Boolean] = None

  override def beforeEach={
    server = actorOf(new Server with TestChallenges with PrintlineScoreBoardProvider).start

    scoreBoardTeam = null
    scoreBoardQuestion = null
    result = None
  }

  "As a client the server" should "give a challenge when you ask for one" in {
    server = actorOf(new Server with SimpleChallenges with PrintlineScoreBoardProvider).start
    val chalange = server !! MoreChallenges(team)

    chalange should be (Some(Question("tester", "")))
  }

  it should "give diffrent challenges" in {
    val challenge = server !! MoreChallenges(team)
    challenge should be (Some(Question("challenge", "")))

    val challenge2 = server !! MoreChallenges(team)
    challenge2 should be (Some(Question("challenge2", "")))
  }

  it should "repeate the diffrent challenges" in{
    server !! MoreChallenges(team) should not be (None)
    server !! MoreChallenges(team) should not be (None)
    val firstQuestion = Some(new Question("challenge", ""))
    server !! MoreChallenges(team) should equal(firstQuestion)
  }

  it should "return wrong it answered wrong" in {
    val chalange = server !! MoreChallenges(team)
    val answerStatus = server !! Answer(team, chalange.get.asInstanceOf[Question], "wrong answer")

    answerStatus should be (Some(Wrong()))
  }

  it should "return correct if answered correctly" in {
    val chalange = server !! MoreChallenges(team)
    val answerStatus = server !! Answer(team, chalange.get.asInstanceOf[Question], "answer")

    answerStatus should be (Some(Correct()))
  }

  it should "give all challenges to all clients in turn" in {
    var chalangeToOne = server !! MoreChallenges(new Team("One"))
    var chalangeToTwo = server !! MoreChallenges(new Team("Two"))

    chalangeToOne should equal(chalangeToTwo)

    chalangeToOne = server !! MoreChallenges(new Team("One"))
    chalangeToTwo = server !! MoreChallenges(new Team("Two"))

    chalangeToOne should equal(chalangeToTwo)
  }



  it should "update the score board when success" in{
    server = actorOf(new Server with TestChallenges with TestScoreBoardService).start
    var chalange = server !! MoreChallenges(team)
    server !! Answer(team, chalange.get.asInstanceOf[Question], "answer")

    scoreBoardTeam should equal(team)
    val challenge = chalange.get.asInstanceOf[Question]
    scoreBoardQuestion should equal(new Question(challenge.question, challenge.content))

    result should be (Some(true))
  }

  it should "update the scoreboard when failed" in {
    server = actorOf(new Server with TestChallenges with TestScoreBoardService).start
    var chalange = server !! MoreChallenges(team)

    val aFalseAnswer = Answer(team, chalange.get.asInstanceOf[Question], "false")
    server !! aFalseAnswer

    scoreBoardTeam should equal(team)
    val challenge = chalange.get.asInstanceOf[Question]
    scoreBoardQuestion should equal(new Question(challenge.question, challenge.content))

    result should be (Some(false))
  }


  class TestScoreBoard extends ScoreBoardService
  {
    def challengeCompleted(teamet: no.bekk.scala.Team, question:Question) = {
      scoreBoardTeam = teamet;
      scoreBoardQuestion = question;
      result = Some(true)
    }

    def challengeFailed(team: Team, question:Question)={
      scoreBoardTeam = team;
      scoreBoardQuestion = question;
      result = Some(false)
    }
  }
  
  trait TestScoreBoardService{
    val scoreBoard = new TestScoreBoard
  }

  trait SimpleChallenges
  {
    val challenges = List(
      new Challenge("tester","",   "", (q:Question, tekst) => tekst.equals("svar")),
      new Challenge("tester2","",  "", (q:Question, tekst) => tekst.equals("svar")),
      new Challenge("tester3","",  "", (q:Question, tekst) => tekst.equals("svar"))
    )
  }

}
