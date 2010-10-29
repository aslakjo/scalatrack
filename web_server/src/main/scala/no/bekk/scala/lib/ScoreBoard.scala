package no.bekk.scala.lib

import _root_.no.bekk.scala.comet.LeretServer
import no.bekk.scala._
import no.bekk.scala.messages._


trait CometServerScoreboardProvider
{
  val scoreBoard = new CometServerScoreboardService
}

class CometServerScoreboardService extends ScoreBoardService
{
  def chalangeCompleted(team: Team, challenge: Challenge) = {
    TeamRegister.registerCompletedChalange(team, new Question(challenge.question), Some(challenge.answer))    
    LeretServer ! new CompletedChallenge(team, challenge)
  }
}