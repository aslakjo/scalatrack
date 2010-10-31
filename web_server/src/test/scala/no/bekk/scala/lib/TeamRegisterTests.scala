package no.bekk.scala.lib

import _root_.no.bekk.scala.snippet.{TestChallenges, TeamSnippet}
import org.scalatest.{FlatSpec, BeforeAndAfterEach}
import org.scalatest.matchers.ShouldMatchers
import scala.xml.NodeSeq

import no.bekk.scala._
import no.bekk.scala.messages._
import net.liftweb.util.BindHelpers._

class TeamRegisterTests extends FlatSpec with ShouldMatchers with BeforeAndAfterEach {
  val team = new Team("test")

  "Team register" should "keep track of partisipating teams" in {

    TestTeamRegister.registerCompletedChalange(team, new Question("a"), Some("a"))
    TestTeamRegister.statusOfQuestionForTeam(team) should be( List((new Question("a"), Some("a")), (new Question("b"), None)) )
  }

  it should "keep track of multiple answers" in {
    TestTeamRegister.registerCompletedChalange(team, new Question("a"), Some("a"))
    TestTeamRegister.registerCompletedChalange(team, new Question("b"), Some("b"))

    TestTeamRegister.statusOfQuestionForTeam(team) should be(
        (new Question("a"), Some("a")) :: (new Question("b"), Some("b")) :: Nil
      )
  }

  it should "not keep more than one question per team" in {
    TestTeamRegister.registerCompletedChalange(team, new Question("a"), Some("a"))
    TestTeamRegister.registerCompletedChalange(team, new Question("a"), Some("a"))

    TestTeamRegister.statusOfQuestionForTeam(team) should be(
        (new Question("a"), Some("a")) :: (new Question("b"), None) ::  Nil
      )
  }

  it should "initalize all teams with all chalanges" in {
    val newTeam = new Team("aslak")

    TestTeamRegister.registerCompletedChalange(newTeam, new Question("a"), Some("a"))

    TestTeamRegister.statusOfQuestionForTeam(newTeam) should be(
        (new Question("a"), Some("a")) :: (new Question("b"), None) :: Nil
      )
  }

  object TestTeamRegister extends TeamRegister with TwoTestChallenges

  trait TwoTestChallenges extends Challenges
  {
    override val challenges = List(
      new Challenge("a","a"),
      new Challenge("b","b")
    )
  }

  override def afterEach() = TestTeamRegister.clear 
}