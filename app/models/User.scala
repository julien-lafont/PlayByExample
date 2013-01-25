package models

case class User(
  id: GHUserID,
  ghInfos: GHInfos
)

case class GHInfos(
  login: String,
  gravatarUrl: String
)
