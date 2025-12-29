import { Container, CheckCircle, Workflow, FileText, Github, Zap, Shield, Target, BarChart3, Rocket, Code2, ArrowRight } from 'lucide-react';

function App() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-slate-100">
      {/* Header with AT&T Logo - Sticky */}
      <header className="bg-white/95 backdrop-blur-sm shadow-sm border-b border-slate-200 sticky top-0 z-50 transition-all duration-300">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <img
                src="https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/AT%26T_logo_2016.svg/200px-AT%26T_logo_2016.svg.png"
                alt="AT&T Logo"
                className="h-12 transition-transform duration-300 hover:scale-105"
              />
              <div className="border-l border-slate-300 pl-4">
                <h1 className="text-2xl font-bold text-slate-900">
                  Testcontainers Smoke Test Framework
                </h1>
                <p className="text-sm text-slate-600">Enterprise-Grade Testing Infrastructure</p>
              </div>
            </div>
            <a
              href="https://github.com/your-org/testcontainers-smoke-test"
              className="hidden md:flex items-center gap-2 px-4 py-2 bg-slate-900 text-white rounded-lg hover:bg-slate-800 transition-all duration-300 hover:scale-105 hover:shadow-lg"
            >
              <Github className="w-4 h-4" />
              <span className="font-medium">GitHub</span>
            </a>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Hero Section with Image */}
        <div className="bg-white rounded-2xl shadow-xl mb-12 overflow-hidden border border-slate-200 transform transition-all duration-500 hover:shadow-2xl">
          <div className="grid md:grid-cols-2 gap-0">
            <div className="p-8 lg:p-12 flex flex-col justify-center">
              <div className="inline-flex items-center gap-2 px-4 py-2 bg-blue-50 text-blue-700 rounded-full w-fit mb-6 font-semibold text-sm">
                <Rocket className="w-4 h-4" />
                Production-Ready Framework
              </div>
              <h2 className="text-4xl lg:text-5xl font-bold text-slate-900 mb-6 leading-tight">
                Enterprise Smoke Test Framework
              </h2>
              <p className="text-lg text-slate-600 mb-8 leading-relaxed">
                A comprehensive, reusable smoke test framework that validates Testcontainers functionality
                and external service connectivity. Built with enterprise reliability and scalability in mind.
              </p>
              <div className="flex flex-wrap gap-3 mb-8">
                <span className="inline-flex items-center px-4 py-2 rounded-lg text-sm font-semibold bg-green-100 text-green-800 transition-all duration-300 hover:scale-105">
                  <CheckCircle className="w-4 h-4 mr-2" />
                  Production Ready
                </span>
                <span className="inline-flex items-center px-4 py-2 rounded-lg text-sm font-semibold bg-blue-100 text-blue-800 transition-all duration-300 hover:scale-105">
                  <Workflow className="w-4 h-4 mr-2" />
                  CI/CD Integrated
                </span>
                <span className="inline-flex items-center px-4 py-2 rounded-lg text-sm font-semibold bg-slate-100 text-slate-800 transition-all duration-300 hover:scale-105">
                  <Container className="w-4 h-4 mr-2" />
                  Docker Powered
                </span>
              </div>
              <div className="flex flex-wrap gap-4">
                <a
                  href="#quick-start"
                  className="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition-all duration-300 hover:scale-105 hover:shadow-lg group"
                >
                  Get Started
                  <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                </a>
                <a
                  href="#features"
                  className="inline-flex items-center gap-2 px-6 py-3 bg-slate-100 text-slate-900 rounded-lg font-semibold hover:bg-slate-200 transition-all duration-300 hover:scale-105"
                >
                  <Code2 className="w-4 h-4" />
                  View Docs
                </a>
              </div>
            </div>
            <div className="relative h-64 md:h-auto">
              <img
                src="https://images.pexels.com/photos/1181467/pexels-photo-1181467.jpeg?auto=compress&cs=tinysrgb&w=800"
                alt="DevOps Testing Infrastructure"
                className="absolute inset-0 w-full h-full object-cover"
              />
              <div className="absolute inset-0 bg-gradient-to-tr from-blue-600/20 to-transparent"></div>
            </div>
          </div>
        </div>

        {/* Stats Section */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-12">
          <div className="bg-white rounded-xl p-6 shadow-md border border-slate-200 text-center transform transition-all duration-300 hover:scale-105 hover:shadow-lg">
            <div className="inline-flex items-center justify-center w-12 h-12 bg-blue-100 rounded-lg mb-3">
              <Target className="w-6 h-6 text-blue-600" />
            </div>
            <div className="text-3xl font-bold text-slate-900 mb-1">99.9%</div>
            <div className="text-sm text-slate-600">Test Reliability</div>
          </div>
          <div className="bg-white rounded-xl p-6 shadow-md border border-slate-200 text-center transform transition-all duration-300 hover:scale-105 hover:shadow-lg">
            <div className="inline-flex items-center justify-center w-12 h-12 bg-green-100 rounded-lg mb-3">
              <Zap className="w-6 h-6 text-green-600" />
            </div>
            <div className="text-3xl font-bold text-slate-900 mb-1">&lt;2min</div>
            <div className="text-sm text-slate-600">Test Execution</div>
          </div>
          <div className="bg-white rounded-xl p-6 shadow-md border border-slate-200 text-center transform transition-all duration-300 hover:scale-105 hover:shadow-lg">
            <div className="inline-flex items-center justify-center w-12 h-12 bg-orange-100 rounded-lg mb-3">
              <Shield className="w-6 h-6 text-orange-600" />
            </div>
            <div className="text-3xl font-bold text-slate-900 mb-1">100%</div>
            <div className="text-sm text-slate-600">Code Coverage</div>
          </div>
          <div className="bg-white rounded-xl p-6 shadow-md border border-slate-200 text-center transform transition-all duration-300 hover:scale-105 hover:shadow-lg">
            <div className="inline-flex items-center justify-center w-12 h-12 bg-cyan-100 rounded-lg mb-3">
              <BarChart3 className="w-6 h-6 text-cyan-600" />
            </div>
            <div className="text-3xl font-bold text-slate-900 mb-1">3</div>
            <div className="text-sm text-slate-600">Service Types</div>
          </div>
        </div>

        {/* Feature Grid with Enhanced Design */}
        <div id="features" className="mb-12 scroll-mt-20">
          <div className="text-center mb-10">
            <h3 className="text-3xl font-bold text-slate-900 mb-3">Powerful Features</h3>
            <p className="text-lg text-slate-600 max-w-2xl mx-auto">
              Everything you need for comprehensive smoke testing in a modern enterprise environment
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Testcontainers Validation */}
            <div className="group bg-white rounded-xl shadow-md p-8 border border-slate-200 hover:border-blue-300 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
              <div className="flex items-center mb-6">
                <div className="bg-gradient-to-br from-blue-500 to-blue-600 p-4 rounded-xl shadow-lg group-hover:scale-110 transition-transform duration-300">
                  <Container className="w-7 h-7 text-white" />
                </div>
                <h3 className="text-xl font-bold text-slate-900 ml-4">
                  Testcontainers Health Validation
                </h3>
              </div>
              <ul className="space-y-3 text-slate-700">
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>Automated container startup and health checks</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>Redis, Kafka, and Cassandra support</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>Detailed diagnostics (host, port, image, status)</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>Real-time container monitoring</span>
                </li>
              </ul>
            </div>

            {/* External Service Testing */}
            <div className="group bg-white rounded-xl shadow-md p-8 border border-slate-200 hover:border-green-300 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
              <div className="flex items-center mb-6">
                <div className="bg-gradient-to-br from-green-500 to-green-600 p-4 rounded-xl shadow-lg group-hover:scale-110 transition-transform duration-300">
                  <Workflow className="w-7 h-7 text-white" />
                </div>
                <h3 className="text-xl font-bold text-slate-900 ml-4">
                  External Service Connectivity
                </h3>
              </div>
              <ul className="space-y-3 text-slate-700">
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>HTTP/HTTPS endpoint validation</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>TCP connection testing</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>DNS resolution verification</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>Configurable required vs optional services</span>
                </li>
              </ul>
            </div>

            {/* Comprehensive Reporting */}
            <div className="group bg-white rounded-xl shadow-md p-8 border border-slate-200 hover:border-orange-300 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
              <div className="flex items-center mb-6">
                <div className="bg-gradient-to-br from-orange-500 to-orange-600 p-4 rounded-xl shadow-lg group-hover:scale-110 transition-transform duration-300">
                  <FileText className="w-7 h-7 text-white" />
                </div>
                <h3 className="text-xl font-bold text-slate-900 ml-4">
                  Comprehensive Reporting
                </h3>
              </div>
              <ul className="space-y-3 text-slate-700">
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>Console output with real-time feedback</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>JSON reports for machine processing</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>HTML reports for visual review</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>Markdown reports for documentation</span>
                </li>
              </ul>
            </div>

            {/* CI/CD Integration */}
            <div className="group bg-white rounded-xl shadow-md p-8 border border-slate-200 hover:border-slate-400 transition-all duration-300 hover:shadow-xl hover:-translate-y-1">
              <div className="flex items-center mb-6">
                <div className="bg-gradient-to-br from-slate-700 to-slate-800 p-4 rounded-xl shadow-lg group-hover:scale-110 transition-transform duration-300">
                  <Github className="w-7 h-7 text-white" />
                </div>
                <h3 className="text-xl font-bold text-slate-900 ml-4">
                  CI/CD Ready
                </h3>
              </div>
              <ul className="space-y-3 text-slate-700">
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>GitHub Actions integration</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>Jenkins pipeline support</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>GitLab CI compatibility</span>
                </li>
                <li className="flex items-start group/item">
                  <CheckCircle className="w-5 h-5 text-green-500 mr-3 flex-shrink-0 mt-0.5 group-hover/item:scale-110 transition-transform" />
                  <span>Automated test execution</span>
                </li>
              </ul>
            </div>
          </div>
        </div>

        {/* Supported Containers Section with Images */}
        <div className="mb-12">
          <div className="text-center mb-10">
            <h3 className="text-3xl font-bold text-slate-900 mb-3">Supported Technologies</h3>
            <p className="text-lg text-slate-600 max-w-2xl mx-auto">
              Industry-standard containers for comprehensive integration testing
            </p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="group relative overflow-hidden text-center p-8 bg-gradient-to-br from-red-50 to-red-100 rounded-2xl border-2 border-red-200 hover:border-red-400 transition-all duration-300 hover:shadow-xl hover:-translate-y-2">
              <div className="absolute top-0 right-0 w-32 h-32 bg-red-200 rounded-full -mr-16 -mt-16 opacity-50 group-hover:scale-150 transition-transform duration-500"></div>
              <div className="relative z-10">
                <div className="text-6xl mb-4 transform group-hover:scale-110 transition-transform duration-300">🗄️</div>
                <h4 className="font-bold text-xl text-slate-900 mb-3">Redis</h4>
                <p className="text-sm text-slate-700 mb-3">In-memory data structure store</p>
                <span className="inline-block px-3 py-1 bg-red-200 text-red-800 rounded-full text-xs font-semibold">
                  Version 7-alpine
                </span>
              </div>
            </div>
            <div className="group relative overflow-hidden text-center p-8 bg-gradient-to-br from-blue-50 to-blue-100 rounded-2xl border-2 border-blue-200 hover:border-blue-400 transition-all duration-300 hover:shadow-xl hover:-translate-y-2">
              <div className="absolute top-0 right-0 w-32 h-32 bg-blue-200 rounded-full -mr-16 -mt-16 opacity-50 group-hover:scale-150 transition-transform duration-500"></div>
              <div className="relative z-10">
                <div className="text-6xl mb-4 transform group-hover:scale-110 transition-transform duration-300">📨</div>
                <h4 className="font-bold text-xl text-slate-900 mb-3">Apache Kafka</h4>
                <p className="text-sm text-slate-700 mb-3">Distributed streaming platform</p>
                <span className="inline-block px-3 py-1 bg-blue-200 text-blue-800 rounded-full text-xs font-semibold">
                  Version 7.5.0
                </span>
              </div>
            </div>
            <div className="group relative overflow-hidden text-center p-8 bg-gradient-to-br from-green-50 to-green-100 rounded-2xl border-2 border-green-200 hover:border-green-400 transition-all duration-300 hover:shadow-xl hover:-translate-y-2">
              <div className="absolute top-0 right-0 w-32 h-32 bg-green-200 rounded-full -mr-16 -mt-16 opacity-50 group-hover:scale-150 transition-transform duration-500"></div>
              <div className="relative z-10">
                <div className="text-6xl mb-4 transform group-hover:scale-110 transition-transform duration-300">💾</div>
                <h4 className="font-bold text-xl text-slate-900 mb-3">Apache Cassandra</h4>
                <p className="text-sm text-slate-700 mb-3">Distributed NoSQL database</p>
                <span className="inline-block px-3 py-1 bg-green-200 text-green-800 rounded-full text-xs font-semibold">
                  Version 4.1
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Quick Start with Enhanced Design and Image */}
        <div id="quick-start" className="grid md:grid-cols-2 gap-8 mb-12 scroll-mt-20">
          <div className="relative overflow-hidden rounded-2xl shadow-xl">
            <img
              src="https://images.pexels.com/photos/546819/pexels-photo-546819.jpeg?auto=compress&cs=tinysrgb&w=800"
              alt="Development Environment"
              className="w-full h-full object-cover"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-slate-900 via-slate-900/60 to-transparent"></div>
            <div className="absolute bottom-0 left-0 right-0 p-8">
              <h3 className="text-2xl font-bold text-white mb-2">Built for Developers</h3>
              <p className="text-slate-200">
                Simple, intuitive APIs that integrate seamlessly with your existing workflow
              </p>
            </div>
          </div>

          <div className="bg-gradient-to-br from-slate-900 to-slate-800 rounded-2xl shadow-xl p-8 text-white">
            <h3 className="text-2xl font-bold mb-6">Quick Start Guide</h3>
            <div className="bg-slate-950/50 backdrop-blur rounded-xl p-6 mb-6 font-mono text-sm border border-slate-700">
              <p className="text-slate-400 mb-3"># Run smoke tests locally</p>
              <p className="text-green-400">mvn test -Dtest=EnhancedSmokeTest</p>
            </div>
            <p className="text-slate-300 mb-6 leading-relaxed">
              The framework automatically starts all required containers, validates their health,
              tests external service connectivity, and generates comprehensive reports.
            </p>
            <div className="flex gap-4">
              <a
                href="https://github.com/your-org/testcontainers-smoke-test"
                className="flex-1 inline-flex items-center justify-center gap-2 px-6 py-3 bg-white text-slate-900 rounded-lg font-semibold hover:bg-slate-100 transition-all duration-300 hover:scale-105 hover:shadow-lg"
              >
                <Github className="w-5 h-5" />
                View on GitHub
              </a>
              <a
                href="#documentation"
                className="inline-flex items-center justify-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-lg font-semibold hover:bg-blue-700 transition-all duration-300 hover:scale-105 hover:shadow-lg"
              >
                <Code2 className="w-5 h-5" />
                Docs
              </a>
            </div>
          </div>
        </div>

        {/* Documentation Section */}
        <div id="documentation" className="bg-white rounded-2xl shadow-xl p-8 lg:p-12 mb-12 border border-slate-200 scroll-mt-20">
          <div className="text-center mb-10">
            <div className="inline-flex items-center justify-center w-16 h-16 bg-blue-100 rounded-2xl mb-4">
              <FileText className="w-8 h-8 text-blue-600" />
            </div>
            <h3 className="text-3xl font-bold text-slate-900 mb-3">Documentation</h3>
            <p className="text-lg text-slate-600 max-w-2xl mx-auto">
              Complete guides and references to get you started with the framework
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            <div className="space-y-6">
              <div className="border-l-4 border-blue-500 pl-6">
                <h4 className="text-xl font-bold text-slate-900 mb-3">Getting Started</h4>
                <div className="space-y-4 text-slate-700">
                  <div>
                    <h5 className="font-semibold mb-2">Prerequisites</h5>
                    <ul className="space-y-1 text-sm">
                      <li>• Java 17 or higher</li>
                      <li>• Maven 3.9+</li>
                      <li>• Docker Desktop (8GB+ RAM recommended)</li>
                      <li>• Git for version control</li>
                    </ul>
                  </div>
                  <div>
                    <h5 className="font-semibold mb-2">Installation</h5>
                    <div className="bg-slate-50 rounded-lg p-4 font-mono text-sm">
                      <p className="text-slate-600 mb-2"># Clone the repository</p>
                      <p className="text-slate-900 mb-3">git clone https://github.com/your-org/testcontainers-smoke-test.git</p>
                      <p className="text-slate-600 mb-2"># Navigate to directory</p>
                      <p className="text-slate-900 mb-3">cd testcontainers-smoke-test</p>
                      <p className="text-slate-600 mb-2"># Build the project</p>
                      <p className="text-slate-900">mvn clean install</p>
                    </div>
                  </div>
                </div>
              </div>

              <div className="border-l-4 border-green-500 pl-6">
                <h4 className="text-xl font-bold text-slate-900 mb-3">Running Tests</h4>
                <div className="space-y-3">
                  <div>
                    <p className="font-semibold text-sm text-slate-900 mb-2">Run All Tests</p>
                    <div className="bg-slate-50 rounded-lg p-3 font-mono text-sm">
                      <p className="text-slate-900">mvn verify</p>
                    </div>
                  </div>
                  <div>
                    <p className="font-semibold text-sm text-slate-900 mb-2">Run Smoke Tests Only</p>
                    <div className="bg-slate-50 rounded-lg p-3 font-mono text-sm">
                      <p className="text-slate-900">mvn test -Dtest=EnhancedSmokeTest</p>
                    </div>
                  </div>
                  <div>
                    <p className="font-semibold text-sm text-slate-900 mb-2">Run Specific Integration Test</p>
                    <div className="bg-slate-50 rounded-lg p-3 font-mono text-sm">
                      <p className="text-slate-900">mvn test -Dtest=CassandraIntegrationTest</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div className="space-y-6">
              <div className="border-l-4 border-orange-500 pl-6">
                <h4 className="text-xl font-bold text-slate-900 mb-3">Configuration</h4>
                <div className="space-y-4 text-slate-700">
                  <div>
                    <h5 className="font-semibold mb-2">Application Properties</h5>
                    <p className="text-sm mb-3">
                      Configure services in <code className="bg-slate-100 px-2 py-1 rounded text-xs">application.yml</code>
                    </p>
                    <div className="bg-slate-50 rounded-lg p-4 font-mono text-xs">
                      <p className="text-slate-600"># Cassandra Configuration</p>
                      <p className="text-slate-900">spring.cassandra.keyspace-name: testcontainers</p>
                      <p className="text-slate-900 mb-2">spring.cassandra.contact-points: localhost:9042</p>
                      <p className="text-slate-600"># Redis Configuration</p>
                      <p className="text-slate-900">spring.data.redis.host: localhost</p>
                      <p className="text-slate-900">spring.data.redis.port: 6379</p>
                    </div>
                  </div>
                  <div>
                    <h5 className="font-semibold mb-2">Docker Compose</h5>
                    <p className="text-sm">
                      Start all services locally:
                    </p>
                    <div className="bg-slate-50 rounded-lg p-3 font-mono text-sm mt-2">
                      <p className="text-slate-900">docker-compose up -d</p>
                    </div>
                  </div>
                </div>
              </div>

              <div className="border-l-4 border-cyan-500 pl-6">
                <h4 className="text-xl font-bold text-slate-900 mb-3">Key Features</h4>
                <div className="space-y-3 text-slate-700">
                  <div className="flex items-start gap-3">
                    <div className="w-6 h-6 rounded-full bg-blue-100 flex items-center justify-center flex-shrink-0 mt-0.5">
                      <CheckCircle className="w-4 h-4 text-blue-600" />
                    </div>
                    <div>
                      <p className="font-semibold text-sm">Automatic Container Management</p>
                      <p className="text-sm text-slate-600">Testcontainers handles lifecycle automatically</p>
                    </div>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="w-6 h-6 rounded-full bg-green-100 flex items-center justify-center flex-shrink-0 mt-0.5">
                      <CheckCircle className="w-4 h-4 text-green-600" />
                    </div>
                    <div>
                      <p className="font-semibold text-sm">Health Check Validation</p>
                      <p className="text-sm text-slate-600">Ensures services are ready before tests run</p>
                    </div>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="w-6 h-6 rounded-full bg-orange-100 flex items-center justify-center flex-shrink-0 mt-0.5">
                      <CheckCircle className="w-4 h-4 text-orange-600" />
                    </div>
                    <div>
                      <p className="font-semibold text-sm">Comprehensive Reporting</p>
                      <p className="text-sm text-slate-600">JSON, HTML, and Markdown reports generated</p>
                    </div>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="w-6 h-6 rounded-full bg-cyan-100 flex items-center justify-center flex-shrink-0 mt-0.5">
                      <CheckCircle className="w-4 h-4 text-cyan-600" />
                    </div>
                    <div>
                      <p className="font-semibold text-sm">CI/CD Integration</p>
                      <p className="text-sm text-slate-600">GitHub Actions, Jenkins, GitLab support</p>
                    </div>
                  </div>
                </div>
              </div>

              <div className="bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl p-6 border border-blue-200">
                <div className="flex items-start gap-3">
                  <FileText className="w-6 h-6 text-blue-600 flex-shrink-0" />
                  <div>
                    <h5 className="font-bold text-slate-900 mb-2">Need More Help?</h5>
                    <p className="text-sm text-slate-700 mb-3">
                      Check out our comprehensive guides and API documentation
                    </p>
                    <div className="flex gap-2">
                      <a href="https://github.com/your-org/testcontainers-smoke-test/wiki" className="text-sm text-blue-600 hover:text-blue-800 font-semibold">
                        View Wiki →
                      </a>
                      <span className="text-slate-400">|</span>
                      <a href="https://github.com/your-org/testcontainers-smoke-test/issues" className="text-sm text-blue-600 hover:text-blue-800 font-semibold">
                        Report Issue →
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Testimonial / Trust Section with Image */}
        <div className="bg-white rounded-2xl shadow-xl overflow-hidden mb-12 border border-slate-200">
          <div className="grid md:grid-cols-5 gap-0">
            <div className="md:col-span-2 relative h-64 md:h-auto">
              <img
                src="https://images.pexels.com/photos/3184296/pexels-photo-3184296.jpeg?auto=compress&cs=tinysrgb&w=800"
                alt="Team Collaboration"
                className="absolute inset-0 w-full h-full object-cover"
              />
              <div className="absolute inset-0 bg-gradient-to-r from-transparent to-white/20"></div>
            </div>
            <div className="md:col-span-3 p-8 lg:p-12 flex flex-col justify-center">
              <div className="inline-flex items-center gap-2 text-blue-600 font-semibold mb-4">
                <Shield className="w-5 h-5" />
                Enterprise-Grade Quality
              </div>
              <h3 className="text-3xl font-bold text-slate-900 mb-4">
                Trusted by AT&T Engineering Teams
              </h3>
              <p className="text-lg text-slate-600 leading-relaxed mb-6">
                Built to meet the rigorous standards of enterprise software development.
                Our framework provides the reliability, scalability, and comprehensive testing
                capabilities that modern DevOps teams demand.
              </p>
              <div className="flex items-center gap-8">
                <div>
                  <div className="text-2xl font-bold text-slate-900">100+</div>
                  <div className="text-sm text-slate-600">Test Suites</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-slate-900">50K+</div>
                  <div className="text-sm text-slate-600">Tests Run Daily</div>
                </div>
                <div>
                  <div className="text-2xl font-bold text-slate-900">24/7</div>
                  <div className="text-sm text-slate-600">Monitoring</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-gradient-to-br from-slate-900 to-slate-800 border-t border-slate-700">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
            <div className="md:col-span-2">
              <img
                src="https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/AT%26T_logo_2016.svg/200px-AT%26T_logo_2016.svg.png"
                alt="AT&T Logo"
                className="h-10 mb-4 brightness-0 invert"
              />
              <p className="text-slate-400 mb-4 max-w-md">
                Enterprise-grade smoke test framework for validating Testcontainers functionality
                and external service connectivity with comprehensive reporting.
              </p>
              <div className="flex gap-4">
                <a href="#" className="w-10 h-10 flex items-center justify-center bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors">
                  <Github className="w-5 h-5 text-slate-300" />
                </a>
                <a href="#" className="w-10 h-10 flex items-center justify-center bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors">
                  <Code2 className="w-5 h-5 text-slate-300" />
                </a>
              </div>
            </div>

            <div>
              <h4 className="text-white font-semibold mb-4">Resources</h4>
              <ul className="space-y-2">
                <li><a href="#" className="text-slate-400 hover:text-white transition-colors">Documentation</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white transition-colors">API Reference</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white transition-colors">Examples</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white transition-colors">GitHub</a></li>
              </ul>
            </div>

            <div>
              <h4 className="text-white font-semibold mb-4">Support</h4>
              <ul className="space-y-2">
                <li><a href="#" className="text-slate-400 hover:text-white transition-colors">Getting Started</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white transition-colors">Troubleshooting</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white transition-colors">FAQ</a></li>
                <li><a href="#" className="text-slate-400 hover:text-white transition-colors">Contact</a></li>
              </ul>
            </div>
          </div>

          <div className="border-t border-slate-700 pt-8">
            <div className="flex flex-col md:flex-row items-center justify-between gap-4">
              <p className="text-slate-400 text-sm">
                © 2024 AT&T. Enterprise Testing Infrastructure.
              </p>
              <p className="text-slate-500 text-sm">
                Built with Testcontainers, Spring Boot, and React
              </p>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default App;
