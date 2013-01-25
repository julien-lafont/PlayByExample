package models

type GHGistID = GHID

case class Question(
  authorID: GHID,
  title: String,
  gistID: GHGistID,
  tags: Seq[Tag],
  createdAt: DateTime,
  updatedAt: DateTime
)
