-- Add skill match columns to analysis_results table
ALTER TABLE analysis_results
    ADD COLUMN skill_match_percent integer,
    ADD COLUMN matched_skills varchar(2000),
    ADD COLUMN missing_skills varchar(2000);
