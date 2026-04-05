create table persons (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    name varchar(120) not null,
    email varchar(190) not null,
    password_hash varchar(255) not null,
    constraint uk_persons_email unique (email)
);

create table resumes (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    title varchar(200) not null,
    language varchar(32) not null,
    target_role varchar(200) not null,
    owner_id uuid not null,
    constraint fk_resumes_owner foreign key (owner_id) references persons (id) on delete cascade
);

create table resume_versions (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    version_number integer not null,
    text_content text,
    source_type varchar(16) not null,
    storage_path varchar(512),
    original_file_name varchar(255),
    content_type varchar(100),
    file_size bigint,
    resume_id uuid not null,
    constraint fk_resume_versions_resume foreign key (resume_id) references resumes (id) on delete cascade,
    constraint uk_resume_version_number unique (resume_id, version_number)
);

create table vacancies (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    title varchar(200) not null,
    company varchar(200),
    url varchar(500),
    text text not null,
    owner_id uuid not null,
    constraint fk_vacancies_owner foreign key (owner_id) references persons (id) on delete cascade
);

create table analysis_jobs (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    status varchar(32) not null,
    error_code integer,
    error_message text,
    started_at timestamp with time zone,
    finished_at timestamp with time zone,
    resume_version_id uuid not null,
    vacancy_id uuid not null,
    constraint fk_analysis_jobs_resume_version foreign key (resume_version_id) references resume_versions (id) on delete cascade,
    constraint fk_analysis_jobs_vacancy foreign key (vacancy_id) references vacancies (id) on delete cascade
);

create table analysis_results (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    score integer not null,
    grade_label varchar(40) not null,
    summary text not null,
    overall_fit_percent integer not null,
    analysis_job_id uuid not null,
    constraint uk_analysis_results_job unique (analysis_job_id),
    constraint fk_analysis_results_job foreign key (analysis_job_id) references analysis_jobs (id) on delete cascade
);

create table problems (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    category varchar(40) not null,
    severity varchar(20) not null,
    fragment text,
    description text not null,
    analysis_result_id uuid not null,
    constraint fk_problems_analysis_result foreign key (analysis_result_id) references analysis_results (id) on delete cascade
);

create table recommendations (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    action text not null,
    example text,
    priority varchar(20) not null,
    analysis_result_id uuid not null,
    constraint fk_recommendations_analysis_result foreign key (analysis_result_id) references analysis_results (id) on delete cascade
);

create index idx_resumes_owner_id on resumes (owner_id);
create index idx_resume_versions_resume_id on resume_versions (resume_id);
create index idx_vacancies_owner_id on vacancies (owner_id);
create index idx_analysis_jobs_resume_version_id on analysis_jobs (resume_version_id);
create index idx_analysis_jobs_vacancy_id on analysis_jobs (vacancy_id);
create index idx_analysis_jobs_status on analysis_jobs (status);
create index idx_problems_analysis_result_id on problems (analysis_result_id);
create index idx_recommendations_analysis_result_id on recommendations (analysis_result_id);
