package models

import org.joda.time._

case class Question(
  authorID: GHID,
  title: String,
  gistID: GHGistID,
  tags: Seq[Tag],
  createdAt: DateTime,
  updatedAt: DateTime
)

case class Tag(name: String) extends AnyVal
