-- Skills table for storing known skills
create table skills (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    name varchar(200) not null,
    category varchar(50) not null,
    normalized_name varchar(200) not null,
    constraint uk_skills_normalized_name unique (normalized_name)
);

-- Skill aliases table for normalization (e.g., java8 -> Java 8, k8s -> Kubernetes)
create table skill_aliases (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    alias varchar(200) not null,
    normalized_alias varchar(200) not null,
    skill_id uuid not null,
    constraint uk_skill_aliases_normalized_alias unique (normalized_alias),
    constraint fk_skill_aliases_skill foreign key (skill_id) references skills (id) on delete cascade
);

-- Skill matches table for storing analysis results
create table skill_matches (
    id uuid primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    match_percent integer not null,
    matched_skills_count integer not null,
    missing_skills_count integer not null,
    matched_skills text,
    missing_skills text,
    analysis_result_id uuid not null,
    constraint fk_skill_matches_analysis_result foreign key (analysis_result_id) references analysis_results (id) on delete cascade
);

create index idx_skill_aliases_skill_id on skill_aliases (skill_id);
create index idx_skill_matches_analysis_result_id on skill_matches (analysis_result_id);
create index idx_skills_category on skills (category);

-- Seed data for programming languages
INSERT INTO skills (id, created_at, updated_at, name, category, normalized_name) VALUES
    (gen_random_uuid(), now(), now(), 'Java', 'PROGRAMMING_LANGUAGE', 'java'),
    (gen_random_uuid(), now(), now(), 'Python', 'PROGRAMMING_LANGUAGE', 'python'),
    (gen_random_uuid(), now(), now(), 'JavaScript', 'PROGRAMMING_LANGUAGE', 'javascript'),
    (gen_random_uuid(), now(), now(), 'TypeScript', 'PROGRAMMING_LANGUAGE', 'typescript'),
    (gen_random_uuid(), now(), now(), 'Kotlin', 'PROGRAMMING_LANGUAGE', 'kotlin'),
    (gen_random_uuid(), now(), now(), 'Go', 'PROGRAMMING_LANGUAGE', 'go'),
    (gen_random_uuid(), now(), now(), 'Rust', 'PROGRAMMING_LANGUAGE', 'rust'),
    (gen_random_uuid(), now(), now(), 'C++', 'PROGRAMMING_LANGUAGE', 'c++'),
    (gen_random_uuid(), now(), now(), 'C#', 'PROGRAMMING_LANGUAGE', 'c#'),
    (gen_random_uuid(), now(), now(), 'Scala', 'PROGRAMMING_LANGUAGE', 'scala'),
    (gen_random_uuid(), now(), now(), 'Ruby', 'PROGRAMMING_LANGUAGE', 'ruby'),
    (gen_random_uuid(), now(), now(), 'PHP', 'PROGRAMMING_LANGUAGE', 'php'),
    (gen_random_uuid(), now(), now(), 'Swift', 'PROGRAMMING_LANGUAGE', 'swift'),
    (gen_random_uuid(), now(), now(), 'SQL', 'PROGRAMMING_LANGUAGE', 'sql');

-- Seed data for frameworks
INSERT INTO skills (id, created_at, updated_at, name, category, normalized_name) VALUES
    (gen_random_uuid(), now(), now(), 'Spring Boot', 'FRAMEWORK', 'spring boot'),
    (gen_random_uuid(), now(), now(), 'Spring Framework', 'FRAMEWORK', 'spring framework'),
    (gen_random_uuid(), now(), now(), 'Django', 'FRAMEWORK', 'django'),
    (gen_random_uuid(), now(), now(), 'Flask', 'FRAMEWORK', 'flask'),
    (gen_random_uuid(), now(), now(), 'React', 'FRAMEWORK', 'react'),
    (gen_random_uuid(), now(), now(), 'Vue.js', 'FRAMEWORK', 'vue.js'),
    (gen_random_uuid(), now(), now(), 'Angular', 'FRAMEWORK', 'angular'),
    (gen_random_uuid(), now(), now(), 'Node.js', 'FRAMEWORK', 'node.js'),
    (gen_random_uuid(), now(), now(), 'Express.js', 'FRAMEWORK', 'express.js'),
    (gen_random_uuid(), now(), now(), 'FastAPI', 'FRAMEWORK', 'fastapi'),
    (gen_random_uuid(), now(), now(), 'Hibernate', 'FRAMEWORK', 'hibernate'),
    (gen_random_uuid(), now(), now(), 'JPA', 'FRAMEWORK', 'jpa'),
    (gen_random_uuid(), now(), now(), 'ASP.NET', 'FRAMEWORK', 'asp.net'),
    (gen_random_uuid(), now(), now(), 'Ruby on Rails', 'FRAMEWORK', 'ruby on rails');

-- Seed data for databases
INSERT INTO skills (id, created_at, updated_at, name, category, normalized_name) VALUES
    (gen_random_uuid(), now(), now(), 'PostgreSQL', 'DATABASE', 'postgresql'),
    (gen_random_uuid(), now(), now(), 'MySQL', 'DATABASE', 'mysql'),
    (gen_random_uuid(), now(), now(), 'MongoDB', 'DATABASE', 'mongodb'),
    (gen_random_uuid(), now(), now(), 'Redis', 'DATABASE', 'redis'),
    (gen_random_uuid(), now(), now(), 'Elasticsearch', 'DATABASE', 'elasticsearch'),
    (gen_random_uuid(), now(), now(), 'Oracle', 'DATABASE', 'oracle'),
    (gen_random_uuid(), now(), now(), 'SQLite', 'DATABASE', 'sqlite'),
    (gen_random_uuid(), now(), now(), 'Cassandra', 'DATABASE', 'cassandra'),
    (gen_random_uuid(), now(), now(), 'DynamoDB', 'DATABASE', 'dynamodb'),
    (gen_random_uuid(), now(), now(), 'ClickHouse', 'DATABASE', 'clickhouse');

-- Seed data for tools
INSERT INTO skills (id, created_at, updated_at, name, category, normalized_name) VALUES
    (gen_random_uuid(), now(), now(), 'Docker', 'TOOL', 'docker'),
    (gen_random_uuid(), now(), now(), 'Kubernetes', 'TOOL', 'kubernetes'),
    (gen_random_uuid(), now(), now(), 'Git', 'TOOL', 'git'),
    (gen_random_uuid(), now(), now(), 'Jenkins', 'TOOL', 'jenkins'),
    (gen_random_uuid(), now(), now(), 'GitHub Actions', 'TOOL', 'github actions'),
    (gen_random_uuid(), now(), now(), 'GitLab CI', 'TOOL', 'gitlab ci'),
    (gen_random_uuid(), now(), now(), 'Maven', 'TOOL', 'maven'),
    (gen_random_uuid(), now(), now(), 'Gradle', 'TOOL', 'gradle'),
    (gen_random_uuid(), now(), now(), 'npm', 'TOOL', 'npm'),
    (gen_random_uuid(), now(), now(), 'Linux', 'TOOL', 'linux'),
    (gen_random_uuid(), now(), now(), 'Nginx', 'TOOL', 'nginx'),
    (gen_random_uuid(), now(), now(), 'Apache Kafka', 'TOOL', 'apache kafka'),
    (gen_random_uuid(), now(), now(), 'RabbitMQ', 'TOOL', 'rabbitmq'),
    (gen_random_uuid(), now(), now(), 'AWS', 'TOOL', 'aws'),
    (gen_random_uuid(), now(), now(), 'Google Cloud', 'TOOL', 'google cloud'),
    (gen_random_uuid(), now(), now(), 'Azure', 'TOOL', 'azure'),
    (gen_random_uuid(), now(), now(), 'Terraform', 'TOOL', 'terraform'),
    (gen_random_uuid(), now(), now(), 'Ansible', 'TOOL', 'ansible'),
    (gen_random_uuid(), now(), now(), 'Prometheus', 'TOOL', 'prometheus'),
    (gen_random_uuid(), now(), now(), 'Grafana', 'TOOL', 'grafana');

-- Seed data for soft skills
INSERT INTO skills (id, created_at, updated_at, name, category, normalized_name) VALUES
    (gen_random_uuid(), now(), now(), 'Teamwork', 'SOFT_SKILL', 'teamwork'),
    (gen_random_uuid(), now(), now(), 'Communication', 'SOFT_SKILL', 'communication'),
    (gen_random_uuid(), now(), now(), 'Leadership', 'SOFT_SKILL', 'leadership'),
    (gen_random_uuid(), now(), now(), 'Problem Solving', 'SOFT_SKILL', 'problem solving'),
    (gen_random_uuid(), now(), now(), 'Critical Thinking', 'SOFT_SKILL', 'critical thinking'),
    (gen_random_uuid(), now(), now(), 'Time Management', 'SOFT_SKILL', 'time management'),
    (gen_random_uuid(), now(), now(), 'Adaptability', 'SOFT_SKILL', 'adaptability'),
    (gen_random_uuid(), now(), now(), 'Agile', 'SOFT_SKILL', 'agile'),
    (gen_random_uuid(), now(), now(), 'Scrum', 'SOFT_SKILL', 'scrum');

-- Seed aliases for common variations
INSERT INTO skill_aliases (id, created_at, updated_at, alias, normalized_alias, skill_id)
SELECT gen_random_uuid(), now(), now(), 'js', 'js', id FROM skills WHERE normalized_name = 'javascript'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'TS', 'ts', id FROM skills WHERE normalized_name = 'typescript'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'Golang', 'golang', id FROM skills WHERE normalized_name = 'go'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'K8s', 'k8s', id FROM skills WHERE normalized_name = 'kubernetes'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'Postgres', 'postgres', id FROM skills WHERE normalized_name = 'postgresql'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'Spring', 'spring', id FROM skills WHERE normalized_name = 'spring boot'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'SpringBoot', 'springboot', id FROM skills WHERE normalized_name = 'spring boot'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'Spring-Boot', 'spring-boot', id FROM skills WHERE normalized_name = 'spring boot'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'NodeJS', 'nodejs', id FROM skills WHERE normalized_name = 'node.js'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'Express', 'express', id FROM skills WHERE normalized_name = 'express.js'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'ReactJS', 'reactjs', id FROM skills WHERE normalized_name = 'react'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'React.js', 'react.js', id FROM skills WHERE normalized_name = 'react'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'Vue', 'vue', id FROM skills WHERE normalized_name = 'vue.js'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'VueJS', 'vuejs', id FROM skills WHERE normalized_name = 'vue.js'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'c++', 'c++', id FROM skills WHERE normalized_name = 'c++'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'C Plus Plus', 'c plus plus', id FROM skills WHERE normalized_name = 'c++'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'CSharp', 'csharp', id FROM skills WHERE normalized_name = 'c#'
UNION ALL
SELECT gen_random_uuid(), now(), now(), 'GCP', 'gcp', id FROM skills WHERE normalized_name = 'google cloud';
