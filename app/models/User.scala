package models

type GHID = String
type GHUserID = GHID

case class User(
  id: GHUserID,
  ghInfos: GHInfos
)

case class GHInfos(
  login: String,
  gravatarUrl: String
)
