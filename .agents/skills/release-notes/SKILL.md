<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
-->
---
name: release-notes
description: Guidance for drafting or updating the per-release notes page under site/src/site/releasenotes/groovy-X.Y.adoc — structure, issue-reference conventions, gapi macro, JDK requirements section, and the JIRA changelog link. Use when adding a new release notes file, expanding an in-progress one, or polishing one ahead of a release.
license: Apache-2.0
compatibility: claude, codex, copilot, cursor, gemini, aider
metadata:
  audience: contributors to apache/groovy-website
  scope: release-notes-authoring
---

# Release notes

Use this skill when drafting or updating a release notes page at
`site/src/site/releasenotes/groovy-X.Y.adoc`. The release notes are
the most-read page on the website around release time and the most
quoted in third-party coverage, so accuracy and tone matter more here
than on most pages.

## When to use this skill

**Use it for:**

- Creating a new `groovy-X.Y.adoc` release notes page.
- Expanding an in-progress one (adding a section for a new feature
  area, a behaviour change, a breaking change).
- Polishing the page ahead of a final release (filling out the
  highlights, tightening prose, verifying issue links).

**Don't use it for:**

- Blog posts or announcement pages — those follow different conventions
  under `site/src/site/blog/` and `site/src/site/pages/`.
- Generator or template changes (`generator/`, `site/src/main/site/`) —
  those are generator-side concerns; this skill is content-only.
- Updating the changelog HTML linked from the bottom of the page — that
  is generated separately, not hand-edited.

## Read first

- [`RELEASE_NOTES_GUIDE.adoc`](../../../RELEASE_NOTES_GUIDE.adoc) —
  **the editorial source of truth** for release notes structure, the
  highlights / modules / topical / breaking-changes / JDK / addendum
  layout, the dual-audience drafting principle (humans + AI; `gapi:`
  on first class mention), enhanced-vs-new framing, and process tips.
  This skill is the working surface; that document is the map.
- [`AGENTS.md`](../../../AGENTS.md) — overall AI-contributor guidance,
  ASF provenance rules, and content conventions.
- [`README.adoc`](../../../README.adoc) — site structure, build, and the
  asf-site branch warning.
- The most recent shipped release notes file (e.g.
  `site/src/site/releasenotes/groovy-5.0.adoc`) — copy its structure,
  attribute block, and link style. New release notes derive from the
  previous file, not from a blank template.

## Top failure modes to avoid

These are the recurring mistakes when working on release notes:

1. **Hallucinating JIRA issue numbers.** Every `GROOVY-NNNN` reference
   must resolve on <https://issues.apache.org/jira/browse/GROOVY>.
   Verify before writing — a wrong number embarrasses publicly.
2. **Hallucinating API symbols, methods, or behaviours.** A release-notes
   bullet that describes a method that doesn't exist, or an option that
   was never added, is worse than no bullet. Cross-check each claim
   against the actual code in `apache/groovy` for the release branch.
3. **Wrong issue-link form.** The convention is
   `link:https://issues.apache.org/jira/browse/GROOVY-NNNN[GROOVY-NNNN]`.
   Bare URLs and inconsistent formatting break the visual flow of the
   page and the rendered footnotes.
4. **Hand-coding `groovy-lang.org/gapi/...` URLs.** Use the `gapi:`
   macro instead — it is processed by `generator/src/main/groovy/generator/LinkMacroProcessor.groovy`
   and keeps links consistent across the site. (Same for any other
   custom link macros the generator defines.)
5. **Skipping the Groovydoc link on first class mention.** The *first*
   time a class appears in the document, link it via `gapi:` so both
   human and AI readers get the fully-qualified name. Subsequent
   mentions can use the short name. This is the dual-audience
   drafting rule — see [`RELEASE_NOTES_GUIDE.adoc`](../../../RELEASE_NOTES_GUIDE.adoc).
6. **Drive-by edits to unrelated sections.** Reformatting an existing
   section "for consistency" while adding a new one buries the real
   change in review and is rejected. Touch only what your task asks for.
7. **Restating the JIRA title verbatim.** A release notes bullet should
   explain *what changed and why a user cares*, not echo the issue
   summary. JIRA titles are written for triage; release notes are
   written for users.
8. **Missing or wrong "Breaking changes" coverage.** Behaviour changes
   that can break user code must be called out, with migration guidance.
   Don't bury them in feature sections. The canonical source list is
   the JIRA filter `labels = breaking AND fixVersion = X.Y.Z` — start
   there rather than `git log` or memory. Major breaking changes get
   their own subsection; the rest are bullets with issue links.
9. **Wrong JDK-requirements section.** Every release notes page ends
   with a JDK requirements block; the build/runtime JDK ranges must
   match what the release branch actually supports, not what the
   previous release supported.
10. **Forgetting the changelog link.** The page ends with a
    "More information" section linking to
    `../changelogs/changelog-X.Y.Z-unreleased.html` (or the released
    variant). Mismatched filenames produce a deadlink at publish time.
11. **Treating an `-alpha` / `-beta` / `-rc` page as final.** Pre-release
    pages are iterated on; preserve `-unreleased` markers and
    placeholder sections rather than deleting them prematurely.
12. **Forgetting that point releases append, not replace.** When `X.0.1`
    or later ships, its notes are added as an `Addendum for X.0.1`
    section *at the end of the same `groovy-X.Y.adoc` page*, not given
    their own file. Same for `X.0.2`, `X.0.3`, ...

## Procedure

1. **Start from the previous release.** Copy
   `site/src/site/releasenotes/groovy-<previous>.adoc` as the
   structural template. Adjust attribute block, version numbers, and
   anchor IDs (`[[GroovyX.Y-...]]`) before editing content.
2. **Source the issue list from JIRA, not from memory.** The JIRA
   filter / changelog for the release version is the authoritative
   list of what's in. Pull from there; don't infer from `git log`
   alone (commits without `GROOVY-NNNN` exist; issues without commits
   on the branch also exist).
3. **Group by user-facing theme, not by issue number.** Bullets
   covering the same area cluster under one section heading
   (e.g. "Extension method additions and improvements", "AST transform
   additions and improvements"). Order sections from most-impactful to
   least; "Breaking changes" goes near the end.
4. **Write each bullet in user-facing terms.** State the change, then
   the user impact, then the issue link. Use code samples for anything
   non-obvious — copy the existing AsciiDoc source-block style:

   ```asciidoc
   [source,groovy]
   ----
   def x = ...
   assert x == ...
   ----
   ```

5. **Use macros, not hand-coded URLs.** `gapi:` for API references;
   `link:https://issues.apache.org/jira/browse/GROOVY-NNNN[GROOVY-NNNN]`
   for issues. Match the form already used in the file.
6. **Update the JDK requirements section.** State the build JDK floor,
   the runtime JDK floor, and the tested JDK range. Match what the
   release branch's CI actually exercises.
7. **Update the "More information" link.** Confirm the
   `changelog-X.Y.Z-...html` filename matches what the changelog
   generator will produce.
8. **Build the user site and check deadlinks.**

   ```
   ./gradlew :site-user:webzip
   ```

   Inspect `site-user/build/reports/` for deadlinks. Eyeball the
   rendered page under `site-user/build/site/releasenotes/` for
   broken includes, mis-rendered tables, or wrapped code blocks.

## Validation checklist

Before declaring the change ready:

- [ ] Every `GROOVY-NNNN` reference resolves on JIRA.
- [ ] Every API symbol, method name, or option mentioned exists in the
      release branch of `apache/groovy`.
- [ ] Issue links use the canonical `link:https://issues.apache.org/jira/browse/GROOVY-NNNN[GROOVY-NNNN]`
      form; no bare URLs, no mismatched display text.
- [ ] API references use the `gapi:` macro, not hand-coded
      `groovy-lang.org/gapi/...` URLs.
- [ ] Bullets describe user-facing change and impact, not raw JIRA titles.
- [ ] Breaking / behaviour changes are in a dedicated section with
      migration guidance, not buried under feature headings.
- [ ] JDK requirements section reflects what this release branch
      actually supports.
- [ ] "More information" changelog link matches the generated filename
      (including `-unreleased` vs released suffix as appropriate).
- [ ] No edits to sections outside the scope of the task.
- [ ] No generated artefacts (`build/`, `*.cache`) staged for commit.
- [ ] `./gradlew :site-user:webzip` succeeds; deadlinks report is clean
      for new/changed links.
- [ ] Commit message references `GROOVY-NNNNN` where applicable; AI
      provenance trailer added if AI tooling assisted.

## Project-specific guidance

The standing editorial guidance for Groovy release notes lives in
[`RELEASE_NOTES_GUIDE.adoc`](../../../RELEASE_NOTES_GUIDE.adoc) at the
repository root. That document is the source of truth for:

- Document structure (Highlights → Modules → Topical → Breaking
  changes → JDK requirements → More information → Addenda).
- Highlights are written *last* even though they appear first.
- Module changes section: maven coordinates for new modules; mention
  removed / deprecated / status-changed modules; skip internal
  modules.
- Topical sections: 1–4 examples each; overflow into a list with a
  pointer to mainstream documentation.
- Enhanced features get a brief recap of what they build on; new
  features get context for why they're worth adding (other languages,
  industry trends).
- Dual-audience drafting (humans + AI), including `gapi:` on first
  class mention.
- Point releases append `Addendum for X.0.N` sections to the same
  `groovy-X.Y.adoc` file rather than getting their own file.

Read that guide before drafting; this skill's failure modes and
procedure are the AI-side guardrails on top of it.

## References

- [`RELEASE_NOTES_GUIDE.adoc`](../../../RELEASE_NOTES_GUIDE.adoc) —
  editorial source of truth (structure, dual-audience drafting,
  enhanced-vs-new framing, addendum convention).
- [`README.adoc`](../../../README.adoc) — site structure and build.
- [`AGENTS.md`](../../../AGENTS.md) — overall AI-contributor guidance.
- `site/src/site/releasenotes/` — all prior release notes; the most
  recent shipped file is the structural template for the next one.
- `generator/src/main/groovy/generator/LinkMacroProcessor.groovy` —
  defines `gapi:` and related link macros.
- `generator/src/main/groovy/generator/AsciidoctorFactory.groovy` —
  AsciiDoctor extension wiring for the site.
- JIRA: <https://issues.apache.org/jira/browse/GROOVY>.
- ASF [Generative Tooling guidance](https://www.apache.org/legal/generative-tooling.html).
